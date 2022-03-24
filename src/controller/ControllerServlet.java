package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import data.DataHandler;
import net.Net;
import net.Training;


@WebServlet("/ControllerServlet")
public class ControllerServlet extends HttpServlet {

  /**
   * Automatisch generierte serialVersionUID.
   */
  private static final long serialVersionUID = 829723344578124002L;

  /**
   * Stellt die Layer und Grundfunktionen des neuronalen Netzes zur Verfügung.
   */
  private Net net;

  /**
   * Trainiert neuronale Netze mit den eingegebenen / geladenen Daten.
   */
  private Training training;

  /**
   * Lädt und formatiert die Daten für die Prognose.
   */
  private DataHandler dataHandler;

  /**
   * Thread, der das Netz trainiert.
   */
  private Thread trainingThread;


  /**
   * @see HttpServlet#HttpServlet()
   */
  public ControllerServlet() {
    super();

    dataHandler = new DataHandler();
  }


  /**
   * Initialisiert das neuronale Netz sowie das dazugehörige Training mit den in
   * der grafischen Oberfläche angegebenen Einstellungen.
   */
  private void initModel(JSONObject json) {
    // Parse user entries
    final double increaseFactor = json.getDouble("increaseFactor");
    final double shrinkFactor = json.getDouble("shrinkFactor");
    final int estimateLength = json.getInt("estimateLength");
    List<Integer> hiddenLayers = makeList(json.getString("hiddenLayers"));

    // Init Model Objects
    net = new Net(estimateLength, hiddenLayers,
        dataHandler.getData().keySet().size());
    training = new Training(net, increaseFactor, shrinkFactor);

    training.setPatterns(dataHandler.getTrainingPatterns(estimateLength));
  }


  /**
   * @return Den String ("Integer, Integer, ...") als Integer-Liste.
   * @throws NumberFormatException Falls beim Parsen etwas schief gelaufen ist.
   */
  private List<Integer> makeList(String listString)
      throws NumberFormatException {
    if (listString.length() == 0)
      return new ArrayList<>();

    String netLayers[] = listString.replaceAll("\\s+", "").split(",");

    List<Integer> res = new ArrayList<>();
    for (int i = 0; i < netLayers.length; i++)
      res.add(Integer.parseInt(netLayers[i]));

    return res;
  }


  /**
   * Startet das Training, falls es nicht bereits läuft. Wenn das Training
   * gestoppt und nicht pausiert ist, wird das neuronale Netz mitsamt der
   * Trainingsparameter neu initialisiert.
   */
  private void startTraining(final JSONObject object) {
    if (trainingThread != null)
      if (trainingThread.isAlive())
        return; // No restart while running

    initModel(object);
    trainingThread = new Thread(() -> training.train(object.getInt("epochs")));
    trainingThread.start();
  }


  /**
   * Stoppt das Training und wartet auf die Beendigung der Threads.
   */
  private void stopTraining() {
    if (trainingThread == null)
      return;

    trainingThread.interrupt();

    try {
      trainingThread.join();
    } catch (InterruptedException ex) {
    }
  }


  private JSONObject bundleNetOutput() throws Exception {
    var data = dataHandler.getData();
    // Die - 1 am Ende braucht man, weil hier die Wachstumsraten ermittelt
    // werden sollen, also braucht man vor dem ersten Wert noch einen
    // Vorgängerwert.
    var inputStart = data.size() - net.getEstimateLength() - 1;

    var input = data.subChart(inputStart);
    var netOut = net.feed(dataHandler.makeInputNetReadable(input));
    var estimateValue = dataHandler.makeOutputHumanReadable(netOut,
        data.last());

    var res = new JSONObject();
    res.put("estimateValue", estimateValue);
    res.put("estimateLength", net.getEstimateLength());
    res.put("currentEpoch",
        training.getEpoch() / (double) training.getMaxEpochs());
    res.put("threadAlive", trainingThread.isAlive());

    return res;
  }


  private JSONObject getRequest(InputStream is) {
    var scanner = new Scanner(is, StandardCharsets.UTF_8);

    String jsonString = "";
    while (scanner.hasNextLine())
      jsonString += scanner.nextLine();

    scanner.close();

    if (jsonString.length() == 0)
      return null;
    else
      return new JSONObject(jsonString);
  }


  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    var jsonRequest = getRequest(request.getInputStream());
    if (jsonRequest == null)
      return;

    var writer = new PrintWriter(new OutputStreamWriter(
        response.getOutputStream(), StandardCharsets.UTF_8), true);

    var description = RequestDescription
        .valueOf(jsonRequest.getString("description"));

    switch (description) {
    case startTraining:
      if (dataHandler.getData() != null)
        startTraining(jsonRequest);
      break;
    case stopTraining:
      stopTraining();
      break;
    case getData:
      stopTraining();
      dataHandler.readData(jsonRequest.getJSONArray("jsonData"),
          jsonRequest.getString("outputValue"));
      break;
    case sendTrainingOutput:
      try {
        writer.println(bundleNetOutput().toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
      break;
    }
  }


  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  @Override
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

}

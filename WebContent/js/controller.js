class Controller {

  constructor() {
    this.buttonTraining = new TrainingButton($('#training-button'));
    this.progressCard = new ProgressCard($('#estimate'));
    this.buttonOpenFile = $('#file-open');
    this.buttonOpenFutureFile = $('#file-future');
    this.title = $('#title');

    this.dataHandler = new DataHandler();
    this.dataSender = new DataSender();

    this.initFileButtons();
    this.initTrainingButton();

    this.stopFlag = false;
  }


  /**
   * Initialisiert die Events, wenn die die Buttons zum Datei-Laden
   * geklickt werden.
   */
  initFileButtons() {
    // Die Buttons #open ("Daten holen") und #future ("Abgleichen")
    // leiten ihre Click-Events auf die file-input-Elemente um.
    $('#open').click(function() { this.buttonOpenFile.click(); }.bind(this));

    // Der Datei-Upload der Hauptdatei
    this.buttonOpenFile.change(async function(event) {
      let file = await this.dataHandler.getFileContent(event.target.files[0]);
      this.dataHandler.loadData(file.name, file.content);

      try {
        // Schätze den Schlusswert.
        await this.dataSender.sendData(this.dataHandler.filterData(), 'end');
        this.title.html('NeuralFinance &ndash; ' + file.name);
        this.dataHandler.showData($('#chart'));
        this.buttonTraining.makeStartButton();
      } catch (error) {
        alert(error);
      }
    }.bind(this));


    $('#future').click(function() { this.buttonOpenFutureFile.click(); }.bind(this));

    // Das Laden der Datei zum Abgleichen
    this.buttonOpenFutureFile.change(async function(event) {
      let file = await this.dataHandler.getFileContent(event.target.files[0]);
      //TODO
    });
  }


  /**
   * Initialisiert die Events, wenn die Buttons zum Kontrollieren des
   * Trainings geklickt werden.
   */
  initTrainingButton() {
    this.buttonTraining.setHandler(function() {
      if (this.buttonTraining.isStartButton()) {
        this.buttonTraining.makeStopButton();
        this.startTraining();
      } else if (this.buttonTraining.isStopButton()) {
        this.buttonTraining.makeStartButton();
        this.stopTraining();
      }
    }.bind(this));
  }


  /**
   * Sendet eine Startanfrage an den Server und leitet
   * die Abfragenschleife für die grafische Ausgabe ein.
   */
  async startTraining() {
    try {
      await this.dataSender.sendStartTraining(this.formInput());
      this.update();
    } catch (error) {
      this.buttonTraining.makeStartButton();
      alert(error);
    }
  }


  /**
   * Sendet eine Stopanfrage an den Server und beendet
   * die Abfragenschleife für die grafische Ausgabe.
   * 
   * @param {boolean} [clientSide = false] Gibt an, ob die Methode
   * nur bezüglich des Clients ausgeführt werden soll, d.h. keine
   * Stopanfrage an den Server gesendet werden soll.
   */
  async stopTraining(clientSide = false) {
    try {
      if (!clientSide) {
        await this.dataSender.sendStopTraining();
        this.stopFlag = true;
      } else {        
        // Falls es sich von alleine beendet, wird die
        // Fortschrittsanzeige noch angepasst.
        this.progressCard.setProgress(1);
      }

      this.buttonTraining.makeStartButton();
    } catch (error) {
      this.buttonTraining.makeStopButton();
      alert(error);
    }
  }


  /**
   * Diese Funktion gibt an, welche Aktionen bei der Abfrageschleife
   * ausgeführt werden sollen, wenn das Training gestartet wurde.
   * 
   * @param {number} [millis = 100] Anzahl der Millisekunden bis zum
   * nächsten Aufruf der Funktion.
   */
  async update(millis = 100) {
    if (this.stopFlag) {
      this.stopFlag = false;  // Mache Flag wieder benutzbar
      return;
    }

    try {
      let trainingOutput = JSON.parse(
        await this.dataSender.getTrainingOutput());

      if (trainingOutput.threadAlive) {  // Solange das Training noch läuft
        let estimate = trainingOutput.estimateValue;
        let lastValue = this.dataHandler.data[this.dataHandler.data.length - 1].end;
        let epoch = trainingOutput.currentEpoch;

        let displayGrowth = round((estimate / lastValue - 1) * 100, 1);
        if (displayGrowth > 0) displayGrowth = '+' + displayGrowth;

        this.progressCard.setUpperText(trainingOutput.estimateLength + ' Datenpunkte');
        this.progressCard.setMainText(round(estimate, 2));
        this.progressCard.setLowerText(displayGrowth + '%');
        this.progressCard.setProgress(epoch);

        // Selbstaufruf über ein Timeout-Objekt.
        setTimeout(this.update.bind(this), millis);
      } else {
        this.stopTraining(true);
      }
    } catch (error) {
      this.stopTraining(true);
      alert(error);
    }
  }


  /**
   * Bündelt die Eingabe
   * - der maximalen Epochenzahl,
   * - des increase factor's,
   * - des shrink factors's,
   * - der Schätzungslänge,
   * - und der Anzahl der versteckten Layer.
   * 
   * @returns {Object} Die Eingabe in der Oberfläche als Objekt
   * gebündelt.
   */
  formInput() {
    return {
			epochs: $('#epochs').val(),
			increaseFactor: $('#increaseFactor').val(),
			shrinkFactor: $('#shrinkFactor').val(),
			estimateLength: $('#estimateLength').val(),
			hiddenLayers: $('#hiddenLayers').val()
		};
  }
}
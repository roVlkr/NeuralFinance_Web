package controller;

public enum RequestDescription {

  /**
   * Befehl, um das Training zu starten.
   */
  startTraining,

  /**
   * Befehl, um das Training stoppen.
   */
  stopTraining,

  /**
   * Befehl, um die Daten zu empfangen.
   */
  getData,

  /**
   * Befehl, um die Netzausgabe zu senden.
   */
  sendTrainingOutput;
}

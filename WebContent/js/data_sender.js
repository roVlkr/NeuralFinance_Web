class DataSender {

  constructor() {

    /**
     * Listet die Beschreibungen der Requests.
     * Dabei kann es vorkommen, dass aus "send" "get"
     * wird und umgekehrt, da die Beschreibungen für den
     * Server lesbar gemacht werden sollen und nicht für
     * den Client.
     */
    this.requests = {
      startTraining: { description: 'startTraining' },
      stopTraining: { description: 'stopTraining' },
      sendData: { description: 'getData' },
      getTrainingOutput: { description: 'sendTrainingOutput' }
    }
  }


  /**
   * Sendet einen JSON-String, der die aus der Datei
   * gelesenen Daten repräsentiert, an den Server.
   * 
   * @param {string} data Die Daten als gültiger JSON-String.
   * @param {string} outputValue Die Bezeichnung für den Wert,
   * der geschätzt werden soll (also für "Eröffnung" hier "start").
   * @throws Falls bei der Abfrage ein Fehler aufgetaucht ist.
   */
  async sendData(data, outputValue) {
    let request = Object.assign({ }, this.requests.sendData);
    request.jsonData = data;
    request.outputValue = outputValue;

    return this.serverRequest(request);
  }

  
  /**
   * Fordert die Ausgabe des neuronalen Netzes an.
   * 
   * @throws Falls bei der Abfrage ein Fehler aufgetaucht ist.
   */
  async getTrainingOutput() {
    return this.serverRequest(this.requests.getTrainingOutput);
  }


  /**
   * Startet das Training des neuronalen Netzes.
   * 
   * @param {Object} formInput Die Eingaben der Benutzeroberfläche.
   * @throws Falls bei der Abfrage ein Fehler aufgetaucht ist.
   */
  async sendStartTraining(formInput) {
    let request = Object.assign({ }, this.requests.startTraining);
    Object.assign(request, formInput);

    return this.serverRequest(request);
  }


  /**
   * Stoppt das Training des neuronalen Netzes.
   * 
   * @throws Falls bei der Abfrage ein Fehler aufgetaucht ist.
   */
  async sendStopTraining() {
    return this.serverRequest(this.requests.stopTraining);
  }


  /**
   * Sendet eine Abfrage ("request") an den Server.
   * 
   * @param {Object} request Die Abfrage in Form eines Objekts.
   */
  serverRequest(request) {
    return new Promise((resolve, reject) => {

      $.post('controller', JSON.stringify(request),
      function(response, status) {
        if (status === 'success') resolve(response);
        else reject(response);
      });

    });
  }
}
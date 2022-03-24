class DataHandler {

  /**
   * Liest den Inhalt einer ASCII-kodierten Datei
   * und gibt ein Promise für den Dateinamen und den
   * Dateiinhalt aus.
   * 
   * @param {File} file Die Datei, die ausgelesen wird.
   * @returns {Promise} Ein Promise, welches bei
   * Erfüllung das Objekt { name: ..., content: ...}
   * zurückgibt.
   */
  getFileContent(file) {
    var reader = new FileReader();

    // Die relevanten Dateien sind in ANSI / ASCII Format
    reader.readAsText(file, 'ascii');

    return new Promise(resolve => {
      reader.onload = function () { 
        resolve( { name: file.name, content: reader.result } );
      };
    });
  }


  /**
   * Speichert die Daten aus der Datei als
   * Array von Objekten ab. Sichert außerdem
   * den Dateinamen.
   * 
   * @param {string} fileName Der Name der Datei.
   * @param {string} fileContent Der Inhalt der Datei.
   */
  loadData(fileName, fileContent) {
    this.fileName = fileName;
    this.data = [];
    
    fileContent.split('\n').forEach(row => {
        try {
          let jsonRow = JSON.parse(row);
          this.dataHasTime = (jsonRow['Zeit'] != undefined); 

          this.data.push({
            date:   new GermanDate(jsonRow['Datum'], jsonRow['Zeit']),
            low:    parseGermanFloat(jsonRow['Tief']),
            start:  parseGermanFloat(jsonRow['Eröffnung']),
            end:    parseGermanFloat(jsonRow['Schluss']),
            high:   parseGermanFloat(jsonRow['Hoch']),
            volume: parseGermanFloat(jsonRow['Volumen'])
          });
        } catch (exception) { /* Wird nicht hinzugefügt. */ }
    });
  }


  /**
   * Formatiert die geladenen Daten in gefilterter Form.
   * Gefiltert heißt in diesem Fall, dass nur die Daten 
   * "Hoch" / "high", "Tief" / "low", "Eröffnung" / "start",
   * "Schluss" / "end" berücksichtigt werden.
   */
  filterData() {
    return this.data.map(row => {
      return {
        low: row.low,
        high: row.high,
        start: row.start,
        end: row.end
      };
    });
  }


  async showData(jqueryChartDiv) {
    let chart = new Chart(jqueryChartDiv[0]);
    await chart.loadAPI();
    chart.show(this.data, this.dataHasTime);
  }
}
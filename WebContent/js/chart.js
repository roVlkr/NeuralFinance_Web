class Chart {

  constructor(chartDiv) {
    google.charts.load('current', { 'packages': ['corechart'] });
    
    this.chartDiv = chartDiv;
    this.options = {
      legend: 'none',
      chartArea: { left: '10%', top: '10%', width: '80%', height: '80%' }
    };
    this.arrayData = [];
  }


  async loadAPI() {
    return new Promise(resolve => google.charts.setOnLoadCallback(resolve));
  }


  show(data, hasTime) {
    let lastRow = data[data.length - 1];

    this.arrayData = data.filter((row, index) => {
      if (hasTime)
        return row.date.getDate() === lastRow.date.getDate() &&
               row.date.getMonth() === lastRow.date.getMonth() &&
               row.date.getFullYear() === lastRow.date.getFullYear() &&
               data.length - index <= 30;
      else {
        return data.length - index <= 30;
      }
    })
    .map(row => [
      row.date, row.low, row.start, row.end, row.high
    ]);


    this.chart = new google.visualization.CandlestickChart(this.chartDiv);
    this.chart.draw(
      google.visualization.arrayToDataTable(this.arrayData, true),
      this.options);
  }
}
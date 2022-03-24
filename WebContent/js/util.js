/**
 * Erweitert die Date-Klasse um das deutsche Datum.
 */
class GermanDate extends Date {

  /**
   * Nimmt einen Datums-String und einen Zeit-String
   * und macht daraus ein Date-Objekt.
   * 
   * @param {string} dateString Das Datum in deutschem Format.
   * @param {string} [timeString] Die Zeit im Format "HH:mm".
   */
  constructor(dateString, timeString) {
    super();

    // Es muss zwingend in dieser Reihenfolge eingetragen werden,
    // denn sonst ist der Monat auf 30 Tage gestellt und aus einer
    // 31 wird der 1. Tag des Monats
    let date = dateString.split('.');
    this.setFullYear(parseInt(date[2]));
    this.setMonth(parseInt(date[1]) - 1);
    this.setDate(parseInt(date[0]));

    if (timeString != undefined) {
      let time = timeString.split(':');
      this.setHours(parseInt(time[0]));
      this.setMinutes(parseInt(time[1]));
    } else {
      this.setHours(8);
      this.setMinutes(0);
    }
    
    this.setSeconds(0);
  }
}


/**
 * Macht aus einer Zeichenkette, die eine Zahl im deutschen Format
 * darstellt, eine normale Zahl.
 * 
 * @param {string} numberString Die Zahl im deutschen
 * Format.
 * @returns {number} Die Zahl, die dabei herauskommt.
 */
function parseGermanFloat(numberString) {
  return parseFloat(numberString
    .replace('.', '')
    .replace(',', '.'));
}


function round(x, decimalPlaces) {
  let potency = Math.pow(10, decimalPlaces);

  return Math.round(x * potency) / potency;
}
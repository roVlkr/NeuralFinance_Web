class ProgressCard {

  /**
   * @param {Object} jqueryObj Das JQuery-Objekt der
   * ProgressCard.
   */
  constructor(jqueryObj) {
    this.circle = jqueryObj.find('path');
    this.paragraph = jqueryObj.find('p');

    this.upperText = '';
    this.mainText = '';
    this.lowerText = '';
  }


  /**
   * 
   * @param {string} text 
   */
  setMainText(text) {
    this.mainText = '<span class="text-lg">' + text + '</span>';
    this.updateText();
  }


  setLowerText(text) {
    this.lowerText = '<span class="text-sm">' + text + '</span>';
    this.updateText();
  }


  setUpperText(text) {
    this.upperText = '<span class="text-sm">' + text + '</span>';
    this.updateText();
  }


  updateText() {
    this.paragraph.html(this.upperText + '<br>' +
      this.mainText + '<br>' + this.lowerText);
  }


  setProgress(percentage) {
    if (percentage >= 1)
      percentage = 0.9999;  // Sonst wird nichts angezeigt

    let offset = 2;
    let size = 100;
    let radius = 50 - offset;

    let x = -Math.cos(-2 * Math.PI * (percentage + 0.25)) * radius + size / 2;
    let y = Math.sin(-2 * Math.PI * (percentage + 0.25)) * radius + size / 2;

    let longFlag = (percentage > 0.5) ? 1 : 0;

    this.circle.attr('d', 'M ' + size / 2 + ' ' + offset +
      ' A ' + radius + ' ' + radius + ' 0 ' + longFlag + ' 1 ' + x + ' ' + y);
  }
}


class TrainingButton {

  constructor(jqueryObj) {
    this.button = jqueryObj;
  }


  setHandler(handler) {
    this.button.click(handler);
  }


  makeStartButton() {
    this.button.prop('disabled', false);
    this.button.addClass('start');
    this.button.removeClass('stop');

    this.button.html('<i class="fas fa-play"></i>');
  }


  makeStopButton() {
    this.button.prop('disabled', false);
    this.button.removeClass('start');
    this.button.addClass('stop');

    this.button.html('<i class="fas fa-stop"></i>');
  }


  isStartButton() {
    return this.button.hasClass('start');
  }


  isStopButton() {
    return this.button.hasClass('stop');
  }
}
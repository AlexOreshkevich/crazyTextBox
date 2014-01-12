package pro.redsoft.demo.textbox.client;

import pro.redsoft.demo.textbox.client.CustomTextBox.KeyProcessingStrategy;

class StrategyProvider {

  KeyProcessingStrategy evenStrategy, oddStrategy;
  int cntr;

  StrategyProvider(CustomTextBox textBox) {
    cntr = 0;
    evenStrategy = new EvenKeyProcessingStrategy(textBox);
    oddStrategy = new OddKeyProcessingStrategy(textBox);
  }

  KeyProcessingStrategy getStrategy() {
    return (++cntr % 2) == 0 ? evenStrategy : oddStrategy;
  }
}
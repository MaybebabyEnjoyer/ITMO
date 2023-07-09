module alu(a, b, control, res);
  input [3:0] a, b; // Операнды
  input [2:0] control; // Управляющие сигналы для выбора операции

  output [3:0] res; // Результат
  // TODO: implementation
endmodule

module d_latch(clk, d, we, q);
  input clk; // Сигнал синхронизации
  input d; // Бит для записи в ячейку
  input we; // Необходимо ли перезаписать содержимое ячейки

  output reg q; // Сама ячейка
  // Изначально в ячейке хранится 0
  initial begin
    q <= 0;
  end
  // Значение изменяется на переданное на спаде сигнала синхронизации
  always @ (negedge clk) begin
    if (we) begin
      q <= d;
    end
  end
endmodule

module register_file(clk, rd_addr, we_addr, we_data, rd_data);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr, we_addr; // Номера регистров для чтения и записи
  input [3:0] we_data; // Данные для записи в регистровый файл

  output [3:0] rd_data; // Данные, полученные в результате чтения из регистрового файла
  // TODO: implementation
endmodule

module calculator(clk, rd_addr, immediate, we_addr, control, rd_data);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr; // Номер регистра, из которого берется значение первого операнда
  input [3:0] immediate; // Целочисленная константа, выступающая вторым операндом
  input [1:0] we_addr; // Номер регистра, куда производится запись результата операции
  input [2:0] control; // Управляющие сигналы для выбора операции

  output [3:0] rd_data; // Данные из регистра c номером 'rd_addr', подающиеся на выход
  // TODO: implementation
endmodule

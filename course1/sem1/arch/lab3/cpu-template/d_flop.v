module d_flop(d, clk, q);
  // данные для записи в регистр
  input [31:0] d;
  // сигнал синхронизации
  input clk;

  // сам регистр
  output reg [31:0] q;

  // изначально заполнен нулем
  initial begin
    q <= 32'b0;
  end

  // запись на фронте сигнала синхронизации
  always @ (posedge clk)
    q <= d;
endmodule

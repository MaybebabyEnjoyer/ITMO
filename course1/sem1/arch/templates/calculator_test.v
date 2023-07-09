`include "calculator.v"

module calculator_test();
  reg [1:0] rd_addr, we_addr;
  reg [2:0] control;
  reg signed [3:0] immediate;
  wire signed [3:0] rd_data;
  reg clk;

  calculator calc(clk, rd_addr, immediate, we_addr, control, rd_data);

  initial begin
    $monitor("rd_data=%d", rd_data);
    // r0 = r0 + 2;
    #5;
    clk = 1;
    // r0 = r0 + 2;
    control = 3'b010;
    immediate = 2;
    rd_addr = 2'b00;
    we_addr = 2'b00;
    #5;
    clk = 0;
    // r1 = r0 - (-2);
    #5;
    clk = 1;
    control = 3'b110;
    rd_addr = 2'b00;
    we_addr = 2'b01;
    immediate = -2;
    #5;
    clk = 0;
    // r2 = r1 & 1
    #5;
    clk = 1;
    control = 3'b000;
    immediate = 1;
    rd_addr = 2'b01;
    we_addr = 2'b10;
    #5;
    clk = 0;
    // r2 = r2 + 0;
    #5;
    clk = 1;
    control = 3'b010;
    immediate = 0;
    rd_addr = 2'b10;
    we_addr = 2'b10;
    #5;
    clk = 0;
  end
endmodule
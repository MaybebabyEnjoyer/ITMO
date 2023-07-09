// модуль, который реализует расширенение
// 16-битной знаковой константы до 32-битной
module sign_extend(in, out);
  input [15:0] in;
  output [31:0] out;

  assign out = {{16{in[15]}}, in};
endmodule

//Расширение 26-битной константы до 32-битной
module extendJump(in, out);
  input [25:0] in;
  output [31:0] out;

  assign out = {6'b000000, in};
endmodule

module and_gate(a, b, out);
  input a, b;
  output reg out;

  always @(a && b) begin
  out = a && b;
  end;
endmodule

// модуль, который реализует побитовый сдвиг числа
// влево на 2 бита
module shl_2(in, out);
  input [31:0] in;
  output [31:0] out;

  assign out = {in[29:0], 2'b00};
endmodule

// 32 битный сумматор
module adder(a, b, out);
  input [31:0] a, b;
  output [31:0] out;

  assign out = a + b;
endmodule

// 32-битный мультиплексор
module mux2_32(d0, d1, a, out);
  input [31:0] d0, d1;
  input a;
  output [31:0] out;
  assign out = a ? d1 : d0;
endmodule

// 5 - битный мультиплексор
module mux2_5(d0, d1, a, out);
  input [4:0] d0, d1;
  input a;
  output [4:0] out;
  assign out = a ? d1 : d0;
endmodule

module mux2_1(d0, d1, a, out);
  input d0, d1;
  input a;
  output out;
  assign out = a ? d1 : d0;
endmodule

module alu(a, b, control, res, zero);
  input signed [31:0] a, b;
  input [2:0] control;
  output reg [31:0] res;
  output reg zero;

  reg [31:0] tempb;

  always @(control or a or b) begin
    if (control[2] == 0) 
    begin 
      tempb = b;
    end else begin
      tempb = ~b;
    end

    case (control[1:0])
      0:
         res = a & tempb;
      1:
         res = a | tempb;
      2:
         res = a + tempb + control[2];
      3:
        if (control[2] == 1) begin
          if (a < b) begin
             res = 1;
          end else begin
             res = 0;
          end
        end 
      endcase

      if (res == 0) begin
        zero = 1;
      end else begin
        zero = 0;
      end
  end

endmodule


module alu(a, b, control, res);
  input [3:0] a, b; // Операнды
  input [2:0] control; // Управляющие сигналы для выбора операции
  output reg [3:0] res; // Результат

  reg [3:0] muxb;

  always @(control or a or b) begin
    case (control[2])
      0: 
        muxb = b;
      1: 
        muxb = ~b;
    endcase

    case (control[1:0])
      0:
         res = a & muxb;
      1:
         res = a | muxb;
      2:
         res = a + muxb + control[2];
      3:
        if (control[2] == 1) begin
          if (a < b) begin
             res = 1;
          end else begin
             res = 0;
          end
        end 
      endcase
  end

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

module register(clk, we, we_data, rd_data);
  input clk; 
  input we;
  input [3:0] we_data; 
  output [3:0] rd_data; 
  
  d_latch dlatch0(clk, we_data[0], we, rd_data[0]);
  d_latch dlatch1(clk, we_data[1], we, rd_data[1]);
  d_latch dlatch2(clk, we_data[2], we, rd_data[2]);
  d_latch dlatch3(clk, we_data[3], we, rd_data[3]);
endmodule

module register_file(clk, rd_addr, we_addr, we_data, rd_data);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr, we_addr; // Номера регистров для чтения и записи
  input [3:0] we_data; // Данные для записи в регистровый файл
  output [3:0] rd_data; // Данные, полученные в результате чтения из регистрового файла
  
  wire [3:0] decoder_out;

  decoder4 decoder(we_addr, decoder_out);
  
  wire [3:0] reg0;
  wire [3:0] reg1;
  wire [3:0] reg2;
  wire [3:0] reg3;
  
  register r0(clk, decoder_out[0], we_data, reg0);
  register r1(clk, decoder_out[1], we_data, reg1);
  register r2(clk, decoder_out[2], we_data, reg2);
  register r3(clk, decoder_out[3], we_data, reg3);
  
  mux24 mux1(rd_addr, reg0, reg1, reg2, reg3, rd_data);
endmodule

module calculator(clk, rd_addr, immediate, we_addr, control, rd_data);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr; // Номер регистра, из которого берется значение первого операнда
  input [3:0] immediate; // Целочисленная константа, выступающая вторым операндом
  input [1:0] we_addr; // Номер регистра, куда производится запись результата операции
  input [2:0] control; // Управляющие сигналы для выбора операции

  output [3:0] rd_data; // Данные из регистра c номером 'rd_addr', подающиеся на выход
  
  wire [3:0] we_data;
  
  alu alu123(rd_data, immediate, control, we_data);
  register_file regfile(clk, rd_addr, we_addr, we_data, rd_data);
  
endmodule


module not_gate(in, out);

  input wire in;
  output wire out;

  supply1 vdd;
  supply0 gnd;

  pmos pmos1(out, vdd, in);
  nmos nmos1(out, gnd, in);
endmodule

module nor_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  supply0 gnd;
  supply1 pwr;

  wire pmos1_out;

  pmos pmos1(pmos1_out, pwr, in1);
  pmos pmos2(out, pmos1_out, in2);
  nmos nmos1(out, gnd, in1);
  nmos nmos2(out, gnd, in2);
endmodule

module or_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire nor_out;

  nor_gate nor_gate1(in1, in2, nor_out);
  not_gate not_gate1(nor_out, out);
endmodule

module nand_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  supply0 gnd;
  supply1 pwr;

  wire nmos1_out;

  pmos pmos1(out, pwr, in1);
  pmos pmos2(out, pwr, in2);
  nmos nmos1(nmos1_out, gnd, in1);
  nmos nmos2(out, nmos1_out, in2);
endmodule

module and_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(in1, in2, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module decoder4(s, z);
    input [1:0] s;
    output [3:0] z;

    wire y0;
    wire x0;

    not_gate not_s0(s[0], y0);
    not_gate not_s1(s[1], x0);

    and_gate an1d(y0, x0, z[0]);
    and_gate an2d(s[0], x0, z[1]);
    and_gate an3d(y0, s[1], z[2]);
    and_gate an4d(s[0], s[1], z[3]);

endmodule

module mux24(s, d_0, d_1, d_2, d_3, y);
  input [1:0] s;
  input [3:0] d_0, d_1, d_2, d_3;
  output [3:0] y;

  wire not_s1, not_s0, s_0, s_1, s_2, s_3, d_0_0, d_0_1, d_0_2, d_0_3, d_1_0, d_1_1, d_1_2, d_1_3, d_2_1, d_2_2, d_2_3, d_3_1, d_3_2, d_3_3;
  wire y_0_0, y_0_1, y_1_0, y_1_1, y_2_0, y_2_1, y_3_0, y_3_1;

  not_gate not1(s[1], not_s1);
  not_gate not2(s[0], not_s0);

  and_gate And_S0(not_s0, not_s1, s_0);
  and_gate And_D0_0(d_0[0], s_0, d_0_0);
  and_gate And_D0_1(d_0[1], s_0, d_0_1);
  and_gate And_D0_2(d_0[2], s_0, d_0_2);
  and_gate And_D0_3(d_0[3], s_0, d_0_3);
  and_gate And_S1(s[0], not_s1, s_1);
  and_gate And_D1_0(d_1[0], s_1, d_1_0);
  and_gate And_D1_1(d_1[1], s_1, d_1_1);
  and_gate And_D1_2(d_1[2], s_1, d_1_2);
  and_gate And_D1_3(d_1[3], s_1, d_1_3);
  and_gate And_S2(not_s0, s[1], s_2);
  and_gate And_D2_0(d_2[0], s_2, d_2_0);
  and_gate And_D2_1(d_2[1], s_2, d_2_1);
  and_gate And_D2_2(d_2[2], s_2, d_2_2);
  and_gate And_D2_3(d_2[3], s_2, d_2_3);
  and_gate And_S3(s[0], s[1], s_3);
  and_gate And_D3_0(d_3[0], s_3, d_3_0);
  and_gate And_D3_1(d_3[1], s_3, d_3_1);
  and_gate And_D3_2(d_3[2], s_3, d_3_2);
  and_gate And_D3_3(d_3[3], s_3, d_3_3);
  or_gate Or_Y_0_1(d_1_0, d_2_0, y_0_0);
  or_gate Or_Y_0_2(d_3_0, d_0_0, y_0_1);
  or_gate Or_end_0(y_0_0, y_0_1, y[0]);
  or_gate Or_Y_1_1(d_1_1, d_2_1, y_1_0);
  or_gate Or_Y_1_2(d_3_1, d_0_1, y_1_1);
  or_gate Or_end_1(y_1_0, y_1_1, y[1]);
  or_gate Or_Y_2_1(d_1_2, d_2_2, y_2_0);
  or_gate Or_Y_2_2(d_3_2, d_0_2, y_2_1);
  or_gate Or_end_2(y_2_0, y_2_1, y[2]);
  or_gate Or_Y_3_1(d_1_3, d_2_3, y_3_0);
  or_gate Or_Y_3_2(d_3_3, d_0_3, y_3_1);
  or_gate Or_end_3(y_3_0, y_3_1, y[3]);
endmodule
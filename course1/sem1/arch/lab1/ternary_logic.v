module ternary_min(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire not_a1;
  wire not_b1;
  wire xor_aa;
  wire xor_bb;
  wire or_ab;
  wire and_1;

  and_gate out_1(a[1], b[1], out[1]);

  not_gate Wnota_1(a[1], not_a1);
  not_gate Wnota_2(b[1], not_b1);
  or_gate Wor_ab(not_a1, not_b1, or_ab);
  xor_gate Wxor_aa(a[1], a[0], xor_aa);
  xor_gate Wxor_bb(b[1], b[0], xor_bb);
  and_gate Wand_1(xor_aa, xor_bb, and_1);

  and_gate out_0(or_ab, and_1, out[0]);
endmodule

module ternary_max(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire or_1;
  wire or_2;

  or_gate Wor_1(a[1], b[1], out[1]);
  or_gate Wor_2(a[0], b[0], or_1);
  and_gate Wand_1(out[1], or_1, or_2);
  xor_gate xor_out(or_2, or_1, out[0]);
endmodule

module ternary_any(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire or_aa;
  wire or_bb;
  wire or_z;
  wire out_1;

  wire or_ab1;
  wire or_ab0;

  wire and_ab0;
  wire a_0;
  wire a_1;
  wire b_0;
  wire b_1;
  wire a_11;
  wire b_1a1;
  wire b_11;
  wire a_1b1;
  wire out_01;

  or_gate Wor_aa(a[0], a[1], or_aa);
  or_gate Wor_bb(b[0], b[1], or_bb);

  and_gate Wor_z(or_aa, or_bb, or_z);

  or_gate Wor_ab1(a[1], b[1], or_ab1);

  and_gate out1 (or_z, or_ab1, out[1]);

  and_gate Wnad_ab0(a[0], b[0], and_ab0);

  not_gate Wa_0(a[0], a_0);
  not_gate Wa_1(a[1], a_1);
  not_gate Wb_0(b[0], b_0);
  not_gate Wb_1(b[1], b_1);

  and_gate Wa_11(a_0, a_1, a_11);
  and_gate Wb_1a1(b[1], a_11, b_1a1);

  and_gate W_b11(b_0, b_1, b_11);
  and_gate Wa_1b1(a[1], b_11, a_1b1);

  or_gate Wout_01(b_1a1, a_1b1, out_01);
  or_gate out_0(out_01, and_ab0, out[0]);
endmodule

module ternary_consensus(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire out_1;
  wire xor_ab1;
  wire xor_ab2;

  and_gate Wout_1(a[1], b[1], out[1]);

  xor_gate Wxor_ab1(a[1], b[1], xor_ab1);
  xor_gate Wxor_ab2(a[0], b[0], xor_ab2);

  or_gate out_01(xor_ab1, xor_ab2, out_1);
  or_gate out_0(a[0], out_1, out[0]);
endmodule


module not_gate(in, out);

  input wire in;
  output wire out;

  supply1 vdd;
  supply0 gnd;

  pmos pmos1(out, vdd, in);
  nmos nmos1(out, gnd, in);
endmodule

module and_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(in1, in2, nand_out);
  not_gate not_gate1(nand_out, out);
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

module xor_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire not_in1;
  wire not_in2;

  wire and_out1;
  wire and_out2;

  wire or_out1;

  not_gate not_gate1(in1, not_in1);
  not_gate not_gate2(in2, not_in2);

  and_gate and_gate1(in1, not_in2, and_out1);
  and_gate and_gate2(not_in1, in2, and_out2);

  or_gate or_gate1(and_out1, and_out2, out);
endmodule
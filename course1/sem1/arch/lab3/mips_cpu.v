`include "util.v"

module control_unit(opcode, funct, MemtoReg, MemWrite, BranchN, BranchE,
                    ALUSrc, RegDst, RegWrite, ALUControl, Jump, Jal, Jr);

input wire [5:0] opcode, funct;
output wire MemtoReg, MemWrite, BranchN, BranchE, ALUSrc, RegDst, RegWrite, Jump, Jal, Jr;
output reg [2:0] ALUControl;

reg tempMemtoReg, tempMemWrite, tempBranchN, tempBranchE, tempALUSrc, tempRegDst, tempRegWrite, tempJump, tempJal, tempJr;

reg [1:0] ALUop;


always @* begin

case (opcode)
  //R-type
  0: begin
    tempRegWrite = 1; tempRegDst = 1; tempALUSrc = 0; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 0;  tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b10; tempJump = 0; tempJr = 0;
  end
  //lw
  35: begin
    tempRegWrite = 1; tempRegDst = 0; tempALUSrc = 1; tempMemtoReg = 1; tempJal = 0;
    tempBranchN = 0; tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b00; tempJump = 0; tempJr = 0;
  end
  //sw
  43: begin
    tempRegWrite = 0; tempRegDst = 0; tempALUSrc = 1; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 0; tempBranchE = 0; tempMemWrite = 1; ALUop = 2'b00; tempJump = 0; tempJr = 0;
  end
  //beq
  4: begin
    tempRegWrite = 0; tempRegDst = 0; tempALUSrc = 0; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 0; tempBranchE = 1; tempMemWrite = 0; ALUop = 2'b01; tempJump = 0; tempJr = 0;
  end
  //bne
  5: begin
    tempRegWrite = 0; tempRegDst = 0; tempALUSrc = 0; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 1; tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b01; tempJump = 0; tempJr = 0;
  end
  //addi
  8: begin
    tempRegWrite = 1; tempRegDst = 0; tempALUSrc = 1; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 0; tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b00; tempJump = 0; tempJr = 0;
  end
  //andi
  12: begin
    tempRegWrite = 1; tempRegDst = 0; tempALUSrc = 1; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 0; tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b11; tempJump = 0; tempJr = 0;
  end
  //j
  2: begin
    tempRegWrite = 0; tempRegDst = 0; tempALUSrc = 0; tempMemtoReg = 0; tempJal = 0;
    tempBranchN = 0; tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b10; tempJump = 1; tempJr = 0;
  end
  //jal
  3: begin
    tempRegWrite = 1; tempRegDst = 0; tempALUSrc = 0; tempMemtoReg = 0; tempJal = 1;
    tempBranchN = 0; tempBranchE = 0; tempMemWrite = 0; ALUop = 2'b10; tempJump = 1; tempJr = 0;
  end
endcase

if (ALUop == 2'b00)
begin
  ALUControl = 3'b010;
end else if (ALUop == 2'b11) 
begin
  ALUControl = 3'b000;
end else if (ALUop == 2'b01)
begin
  ALUControl = 3'b110;
end else begin
  case (funct)
  6'b100000: ALUControl = 3'b010;
  6'b100010: ALUControl = 3'b110;
  6'b100100: ALUControl = 3'b000;
  6'b100101: ALUControl = 3'b001;
  6'b101010: ALUControl = 3'b111;
  6'b001000: begin 
    tempJr = 1;
    tempRegWrite = 0;
  end
  endcase
end
end

assign MemtoReg = tempMemtoReg;
assign RegDst = tempRegDst;
assign ALUSrc = tempALUSrc;
assign RegWrite = tempRegWrite;
assign BranchE = tempBranchE;
assign MemWrite = tempMemWrite;
assign BranchN = tempBranchN;
assign Jump = tempJump;
assign Jal = tempJal;
assign Jr = tempJr; 

endmodule


module mips_cpu(clk, pc, pc_new, instruction_memory_a, instruction_memory_rd, data_memory_a, data_memory_rd, data_memory_we, data_memory_wd,
                register_a1, register_a2, register_a3, register_we3, register_wd3, register_rd1, register_rd2);
  // сигнал синхронизации
  input clk;
  // текущее значение регистра PC
  inout [31:0] pc;
  // новое значение регистра PC (адрес следующей команды)
  output [31:0] pc_new;
  // we для памяти данных
  output data_memory_we;
  // адреса памяти и данные для записи памяти данных
  output [31:0] instruction_memory_a, data_memory_a, data_memory_wd;
  // данные, полученные в результате чтения из памяти
  inout [31:0] instruction_memory_rd, data_memory_rd;
  // we3 для регистрового файла
  output register_we3;
  // номера регистров
  output [4:0] register_a1, register_a2, register_a3;
  // данные для записи в регистровый файл
  output [31:0] register_wd3;
  // данные, полученные в результате чтения из регистрового файла
  inout [31:0] register_rd1, register_rd2;

  assign instruction_memory_a = pc;

  //Разбираемся с текущей командой.
  wire [5:0] opcode = instruction_memory_rd[31:26];
  wire [5:0] funct = instruction_memory_rd[5:0];
  wire [4:0] r1 = instruction_memory_rd[25:21]; //адрес первого операнда
  wire [4:0] r2 = instruction_memory_rd[20:16]; //адрес второго операнда
  wire [4:0] rw1 = instruction_memory_rd[20:16];
  wire [4:0] rw2 = instruction_memory_rd[15:11];
  wire [15:0] tempConst = instruction_memory_rd[15:0];
  wire [25:0] jumpAddr = instruction_memory_rd[25:0];

  //Получаем управляющие значения
  wire MemtoReg, MemWrite, BranchN, BranchE, ALUSrc, RegDst, RegWrite, Jump, Jal, Jr;
  wire [2:0] ALUControl;
  control_unit CU(opcode, funct, MemtoReg, MemWrite, BranchN, BranchE, ALUSrc, RegDst, RegWrite, ALUControl, Jump, Jal, Jr);

  //запрос на чтение регистров
  assign register_a1 = r1;
  assign register_a2 = r2;
  wire [4:0] tempRegWrite;
  mux2_5 writeAdress1(rw1, rw2, RegDst, tempRegWrite);
  mux2_5 writeAdress2(tempRegWrite, 5'b11111, Jal, register_a3);

  wire [31:0] const;

  //расширяем константу
  sign_extend se(tempConst, const);

  //Выбираем операнды в алу
  wire [31:0] srcA = register_rd1;
  wire [31:0] srcB;
  mux2_32 secondOperand(.d0(register_rd2), .d1(const), .a(ALUSrc), .out(srcB));

  //производим операцию
  wire [31:0] aluResult;
  //alures = 0 => zero = 1;
  wire zero;
  alu al1(srcA, srcB, ALUControl, aluResult, zero);

  //записываеи результаты в память и регистры
  assign register_we3 = RegWrite;
  assign data_memory_a = aluResult;
  assign data_memory_wd = register_rd2;
  assign data_memory_we = MemWrite;
  wire [31:0] tempRegZnach;
  mux2_32 writeReg1(aluResult, data_memory_rd, MemtoReg, tempRegZnach);
  mux2_32 writeReg2(tempRegZnach, pc0, Jal, register_wd3);

  //Выбираем новый pc. Считаем с значение с ветвлением и без.
  wire [31:0] pc0;
  wire [31:0] pc1;
  wire [31:0] constMul4;
  shl_2 s(const, constMul4);
  wire musor1, musor2;
  alu al2(pc, 4, 3'b010, pc0, musor1);
  alu al3(pc0, constMul4, 3'b010, pc1, musor2);

  wire BranchChoose;

  //Ветвление в зависимости от команды BNE/BEQ. Если результат АЛУ не 0(zero = 0), то BEQ(BranchE) нас уже не интересует.
  mux2_1 mx1(BranchN, BranchE, zero, BranchChoose);

  wire notZero;
  assign notZero = !zero;
  
  wire rightZero;
  //Изменияем флажок в зависимости от того, какая команда ветвления нас интересует.
  mux2_1 mx2(notZero, zero, BranchE, rightZero);

  wire PCSrc;
  and_gate n1(BranchChoose, rightZero, PCSrc);

  wire [31:0] tempPc;

  //выбираем, выполняется ли ветвление или нет и ставим значение PC(пока что temp, т.к нужно проверить J-инструкции)
  mux2_32 choosePC(pc0, pc1, PCSrc, tempPc);

  //Смотрим, нужно ли прыгнуть куда-то и в зависимости от этого выбираем новый PC.
  wire [31:0] tempJump;
  extendJump se1(jumpAddr, tempJump);

  wire [31:0] jumpExt;
  shl_2 sl1(tempJump, jumpExt);

  wire [31:0] tempPc2;

  mux2_32 choosePC1(tempPc, jumpExt, Jump, tempPc2);

  mux2_32 choosePC2(tempPc2, srcA, Jr, pc_new);
  

endmodule

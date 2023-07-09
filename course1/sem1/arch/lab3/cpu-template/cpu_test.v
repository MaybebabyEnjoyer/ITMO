`include "mips_cpu.v"
`include "memory.v"
`include "register_file.v"
`include "d_flop.v"

module cpu_test();
  reg clk;

  // Инициализируем процессор
  wire [31:0] instruction_memory_a, instruction_memory_rd;

  instruction_memory cpu_instruction_memory(.a(instruction_memory_a), .rd(instruction_memory_rd));

  wire data_memory_we;
  wire [31:0] data_memory_a, data_memory_rd, data_memory_wd;
  wire [31:0] pc, pc_new;

  d_flop program_counter(.d(pc_new), .clk(clk), .q(pc));

  data_memory cpu_data_memory(.a(data_memory_a), .we(data_memory_we), .clk(clk), .wd(data_memory_wd), .rd(data_memory_rd));

  wire register_we3;
  wire [4:0] register_a1, register_a2, register_a3;
  wire [31:0] register_rd1, register_rd2, register_wd3;

  register_file cpu_register(.clk(clk),
                             .we3(register_we3),
                             .a1(register_a1),
                             .a2(register_a2),
                             .a3(register_a3),
                             .wd3(register_wd3),
                             .rd1(register_rd1),
                             .rd2(register_rd2));

  mips_cpu cpu(.clk(clk),
               .pc(pc),
               .pc_new(pc_new),
               .instruction_memory_a(instruction_memory_a),
               .instruction_memory_rd(instruction_memory_rd),
               .data_memory_a(data_memory_a),
               .data_memory_rd(data_memory_rd),
               .data_memory_we(data_memory_we),
               .data_memory_wd(data_memory_wd),
               .register_a1(register_a1),
               .register_a2(register_a2),
               .register_a3(register_a3),
               .register_we3(register_we3),
               .register_wd3(register_wd3),
               .register_rd1(register_rd1),
               .register_rd2(register_rd2));

  // Testbench
  reg [31:0] i_counter, reg_counter, mem_counter;
  initial begin
    // Выполняем 30 тактов
    for (i_counter = 0; i_counter < 30; i_counter = i_counter + 1) begin
      #5
      clk = 1;
      #5
      clk = 0;
    end
    // Дампим регистры
    for (reg_counter = 0; reg_counter < 32; reg_counter = reg_counter + 1) begin
      $display("Register: %d, value: %d", reg_counter, cpu_register.mem[reg_counter]);
    end
    // Дампим память данных
    for (mem_counter = 0; mem_counter < 64; mem_counter = mem_counter + 1) begin
      $display("Addr: %d, value: %d", mem_counter * 4, cpu_data_memory.ram[mem_counter]);
    end
  end
endmodule

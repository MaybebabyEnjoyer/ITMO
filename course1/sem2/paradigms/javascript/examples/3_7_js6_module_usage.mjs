section("Usage");


println("Import selected");

import { exportedConst } from "3_7_js6_module.mjs";
println(`\texportedConst = ${exportedConst}`);


println("Import all");

import * as mod from "3_7_js6_module.mjs";

println(`\tmod.localConst -> ${mod.localConst}`);
println(`\tmod.exportedConst -> ${mod.exportedConst}`);
println(`\tmod.exportedFunction() -> ${mod.exportedFunction()}`);

try {
    println(mod.localFunction());
} catch (e) {
    println(e);
}

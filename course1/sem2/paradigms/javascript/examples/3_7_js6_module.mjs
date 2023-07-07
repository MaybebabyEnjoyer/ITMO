chapter("Modules");

section("Definition");

println("Local definitions");

const localConst = "local";
function localFunction() {
    println(localConst);
}


println("Exported definitions");

export const exportedConst = "exported";
export function exportedFunction() {
    println('\t\texportedFunction: ' + exportedConst);
    return "hello";
}

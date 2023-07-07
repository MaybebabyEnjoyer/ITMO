"use strict";

chapter("Simple I/O example");

for (let i = 1; ; i++) {
    const line = readLine(`Input line ${i}: `);
    if (line == null || line === '') {
        break;
    }
    println(`line = '${line}'`);
}

"use strict";

chapter("Stacks");

const stacks = (function(){
    function AbstractStack(name) {
        this.name = name;
    }
    AbstractStack.prototype.isEmpty = function() {
        return this.size() === 0;
    };
    AbstractStack.prototype.peek = function() {
        const value = this.pop();
        this.push(value);
        return value;
    };

    function ArrayStack(name) {
        AbstractStack.call(this, name);
        const elements = [];
        this.size = function() {
            return elements.length;
        };
        this.push = elements.push.bind(elements);
        this.pop = elements.pop.bind(elements);
    }
    ArrayStack.prototype =
        Object.create(AbstractStack.prototype);

    function LinkedStack(name) {
        AbstractStack.call(this, name);
        this._head = null;
        this._size = 0;
    }
    LinkedStack.prototype = Object.create(AbstractStack.prototype);
    LinkedStack.prototype.size = function() {
        return this._size;
    };
    LinkedStack.prototype.push = function(value) {
        this._size++;
        this._head = {next: this._head, value: value};
    };
    LinkedStack.prototype.pop = function() {
        this._size--;
        const value = this._head.value;
        this._head = this._head.next;
        return value;
    };

    return {
        ArrayStack: ArrayStack,
        LinkedStack: LinkedStack
    }
})();

function testStack(Stack) {
    const stack = new Stack("test");
    println("Testing stack");
    for (let i = 0; i < 5; i++) {
        stack.push(i);
    }
    while (!stack.isEmpty()) {
        print(stack.pop() + " ");
    }
    println();
}

section("ArrayStack");
testStack(stacks.ArrayStack);

section("LinkedStack");
testStack(stacks.LinkedStack);

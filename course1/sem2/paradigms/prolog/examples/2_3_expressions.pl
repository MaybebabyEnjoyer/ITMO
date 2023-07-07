%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Арифметические выражения %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Из lists.pl
lookup(K, [(K, V) | _], V).
lookup(K, [_ | T], V) :- lookup(K, T, V).


% Конструкторы
variable(Name, variable(Name)).
const(Value, const(Value)).

add(A, B, bin(add, A, B)).
sub(A, B, bin(sub, A, B)).
mul(A, B, bin(mul, A, B)).
div(A, B, bin(div, A, B)).

example(E) :-
    variable(x, Vx), variable(y, Vy), variable(z, Vz), const(100, C),
    sub(Vy, Vz, Syz), mul(Vx, Syz, MxSyz), add(MxSyz, C, E).
/*
?- example(E).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/


% Определение операций
bin(add, A, B, R) :- R is A + B.
bin(sub, A, B, R) :- R is A - B.
bin(mul, A, B, R) :- R is A * B.
bin(div, A, B, R) :- R is A / B.

% Вычисление выражений
eval(const(Value), _, Value).
eval(variable(Name), Vars, R) :- lookup(Name, Vars, R).
eval(bin(Op, A, B), Vars, R) :- 
    eval(A, Vars, AV), 
    eval(B, Vars, BV), 
    bin(Op, AV, BV, R).

/*
?- example(E), eval(E, [(x, 1), (y, 2), (z, 3)], R).
   R / 99
*/


% Упрощение операций
simplify_bin_(bin(Op, const(A), const(B)), const(R)) :- eval(bin(Op, const(A), const(B)), _, R).
simplify_bin_(bin(add, A, const(0)), A).
simplify_bin_(bin(add, const(0), B), B).
simplify_bin_(bin(sub, A, const(0)), A).
simplify_bin_(bin(mul, A, const(1)), A).
simplify_bin_(bin(mul, const(1), B), B).
simplify_bin_(bin(mul, _, const(0)), const(0)).
simplify_bin_(bin(mul, const(0), _), const(0)).
simplify_bin_(bin(div, A, const(1)), A).
simplify_bin_(bin(div, const(0), _), const(0)).

simplify_bin(E, R) :- simplify_bin_(E, R), !.
simplify_bin(E, E).


/*
?- simplify_bin(bin(add,const(1), const(2)), R).
   const(3)
?- example(E), add(const(0), E, EE), simplify_bin(EE, R), E=R.
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   EE / bin(add,const(0),bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100)))
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
?- example(E), mul(const(0), E, EE), simplify_bin(EE, R).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   EE / bin(mul,const(0),bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100)))
   R / const(0)
?- example(E), simplify_bin(E, R), E = R.
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/


% Рекурсивное упрощение

simplify(bin(Op, A, B), R) :- !, 
        simplify(A, AS), 
        simplify(B, BS), 
        simplify_bin(bin(Op, AS, BS), R).
simplify(E, E).

% (2 * x - 3)'
simplify_example(R) :- simplify(
    bin(sub, 
        bin(add, 
            bin(mul, const(0), variable(x)), 
            bin(mul, const(2), const(1))
        ), 
        const(0)
    ), 
    R
).

/*
?- simplify_example(R).
   R / const(2)
*/  

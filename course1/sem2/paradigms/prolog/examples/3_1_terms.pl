%%%%%%%%%%%%%%%%%%%%%%%%%
% Преобразование в терм %
%%%%%%%%%%%%%%%%%%%%%%%%%

% Из 2_3_expressions.pl
example(bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))).

% Преобразование в терм
expr_term(variable(Name), Name) :- atom(Name).
expr_term(const(Value), Value) :- number(Value).
expr_term(bin(Op, A, B), R) :- R =.. [Op, AT, BT], expr_term(A, AT), expr_term(B, BT).

/*
?- expr_term(variable(x), T), expr_term(R, T).
   T / x
   E / variable(x)
?- expr_term(const(123), T), expr_term(R, T).
   T / 123
   E / const(123)
?- expr_term(bin(add, const(123), variable(x)), T), expr_term(E, T).
   T / add(123,x)
   E / bin(add,const(123),variable(x))
?- example(E), expr_term(E, T), expr_term(E, T).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   T / add(mul(x,sub(y,z)),100)
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/

% Преобразование в текст
expr_text(E, S) :- ground(E), expr_term(E, T), text_term(S, T).
expr_text(E, S) :-   atom(S), text_term(S, T), expr_term(E, T).

/*
?- expr_text(variable(x), T), expr_text(E, T).
   T / x
   E / variable(x)
?- expr_text(const(123), T), expr_text(E, T).
   T / '123'
   E / const(123)
?- expr_text(bin(add, const(123), variable(x)), T), expr_text(E, T).
   T / 'add(123,x)'
   E / bin(add,const(123),variable(x))
?- example(E), expr_text(E, T), expr_text(E, T).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   T / 'add(mul(x,sub(y,z)),100)'
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
?- expr_text(E, 'add(mul(x,sub(y,z)),100)'), eval(E, [(x, 1), (y, 2), (z, 3)], E).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   E / 99
*/

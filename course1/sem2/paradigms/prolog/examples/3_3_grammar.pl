%%%%%%%%%%%%%%
% Грамматики %
%%%%%%%%%%%%%%

% Из 1_2_expressions.pl
example(bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))).

% Из terms.pl
nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).


% Загружаем библиотеку (DCG = Definite Clause Grammar)
:- load_library('alice.tuprolog.lib.DCGLibrary').

% Грамматика для переменных
expr_p(variable(Name)) --> [Name], { member(Name, [x, y, z]) }.
/*
?- phrase(expr_p(variable(x)), C), phrase(expr_p(R), C).
   C / [x]
   R / variable(x)
*/

% Грамматика для констант
expr_p(const(Value)) -->
  { nonvar(Value, number_chars(Value, Chars)) },
%  { all_member(Chars, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'] },
  digits_p(Chars),
  { Chars = [_ | _], number_chars(Value, Chars) }.

% К сожалению 2Prolog падает при попытке использовать all_members
% Так что перепишем рекурсивно
digits_p([]) --> [].
digits_p([H | T]) -->
  { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'])},
  [H],
  digits_p(T).

/*
?- phrase(expr_p(const(123)), C), phrase(expr_p(R), C).
   C / ['1','2','3']
   R / 123
*/

% Грамматика для символов операций
op_p(add) --> ['+'].
op_p(sub) --> ['-'].
op_p(mul) --> ['*'].
op_p(div) --> ['/'].

% Грамматика для бинарных операций
expr_p(bin(Op, A, B)) --> op_p(Op), ['('], expr_p(A), [','], expr_p(B), [')'].

/*
?- phrase(expr_p(bin(add, const(123), variable(x))), C), phrase(expr_p(R), C).
   C / ['+','(','1','2','3',',',x,')']
   R / bin(add,const(123),variable(x))
?- example(E), phrase(expr_p(E), C), phrase(expr_p(R), C).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   C / ['+','(','*','(',x,',','-','(',y,',',z,')',')',',','1','0','0',')']
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/

% Преобразование в строку
expr_str(E, A) :- ground(E), phrase(expr_p(E), C), atom_chars(A, C).
expr_str(E, A) :-   atom(A), atom_chars(A, C), phrase(expr_p(E), C).

/*
?- example(E), expr_str(E, A), expr_str(R, A).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   S / '+(*(x,-(y,z)),100)'
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/

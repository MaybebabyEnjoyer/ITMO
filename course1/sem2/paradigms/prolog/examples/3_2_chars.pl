%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Преобразование через списки символов %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Из 2_3_expressions.pl
example(bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))).

% Преобразование переменных и констант в список символов
expr_chars_(variable(Name), [Char]) :- atom_chars(Name, [Char]).
expr_chars_(const(Value), Chars) :- number_chars(Value, Chars).

/*
?- expr_chars_(variable(x), C), expr_chars_(E, C).
   C / [x]
   E / variable(x)
?- expr_chars_(const(123), C), expr_chars_(E, C).
   C / ['1','2','3']
   E / const(123)
*/

% Вспомогательные функции
flatten([], []).
flatten([H | T], R) :- append(H, FT, R), flatten(T, FT).

op_chars(add, ['+']).
op_chars(sub, ['-']).
op_chars(mul, ['*']).
op_chars(div, ['/']).

% Преобразование бинарных операций в список символов
expr_chars_(bin(Op, A, B), C) :-
  op_chars(Op, OpC),
  expr_chars_(A, AC),
  expr_chars_(B, BC),
  flatten([OpC, ['('], AC, [','], BC, [')']], C).

/*
?- expr_chars_(bin(add, const(123), variable(x)), C).
   C / ['+','(','1','2','3',',',x,')']
?- example(E), expr_chars_(E, C).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   C / ['+','(','*','(',x,',','-','(',y,',',z,')',')',',','1','0','0',')']
*/

% Преобразование выражения в атом
expr_atom_(E, A) :- expr_chars_(E, C), atom_chars(A, C).

/*
?- example(E), expr_atom_(E, A).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   A / '+(*(x,-(y,z)),100)'
*/


% Разбор строк (обратимые преобразования)

/*
?- expr_chars_(variable(R), [x]).
   R / x    % Ok
?- expr_chars_(variable(R), ['1']).
   R / '1'  % Не ok
*/

% Добавим проверку
expr_chars(variable(Name), [Char]) :-
    atom_chars(Name, [Char]),
    member(Name, [x, y, z]).

/*
?- expr_chars(variable(R), [x]).
   R / x
?- expr_chars(variable(R), ['1']).
   no
?- expr_chars(variable(x), C), expr_chars(R, C).
   C / [x]
   R / variable(x)
*/


/*
?- expr_chars_(const(R), ['1', '2', '3']).
   R / 123  % Ok
?- expr_chars_(const(R), [x]).
   % Не ok
   Domain error in argument 2 of number_chars(Value_e1,[x])
?- expr_chars_(const(R), []).
   % Не ok
   Domain error in argument 2 of number_chars(Value_e1,[])
*/

% Вспомогательные функции
all_member([], _).
all_member([H | T], Values) :- member(H, Values), all_member(T, Values).

nonvar(V, T) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

% Сделаем первое преобразование необязательным и добавим проверку
expr_chars(const(Value), Chars) :-
  nonvar(Value, number_chars(Value, Chars)),
  all_member(Chars, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']), Chars = [_ | _],
  number_chars(Value, Chars).

/*
?- expr_chars(const(R), ['1', '2', '3']).
   R / 123
?- expr_chars(const(R), [x]).
   no
?- expr_chars(const(R), []).
   no
?- expr_chars(const(123), C), expr_chars(R, C).
   C / ['1','2','3']
   R / const(123)
*/

% Сделаем первое преобразование необязательным и добавим проверку
expr_chars(bin(Op, A, B), C) :-
  op_chars(Op, OpC),
  nonvar(A, expr_chars(A, AC)),
  nonvar(B, expr_chars(B, BC)),
  flatten([OpC, ['('], AC, [','], BC, [')']], C),
  expr_chars(A, AC),
  expr_chars(B, BC).

/*
?- expr_chars(bin(add, const(123), variable(x)), C), expr_chars(E, C).
   C / ['+','(','1','2','3',',','2',')']
   E / bin(add,const(123),variable(x))
?- example(E), expr_chars(E, C), expr_chars(R, C).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   C / ['+','(','*','(',x,',','-','(',y,',',z,')',')',',','1','0','0',')']
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/

% Обратимое преобразование в атом
expr_atom(E, A) :- ground(E), expr_chars(E, C), atom_chars(A, C).
expr_atom(E, A) :-   atom(A), atom_chars(A, C), expr_chars(E, C).

/*
?- example(E), expr_atom(E, A), expr_atom(R, A).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   A / '+(*(x,-(y,z)),100)'
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
?- expr_atom(E, '+(*(x,-(y,z)),100)'), eval(E, [(x, 1), (y, 2), (z, 3)], R).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   R / 99
*/

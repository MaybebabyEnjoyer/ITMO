%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Правила высшего порядка %
%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Преобразование

map([], _, []).
map([H | T], F, [RH | RT]) :- G =.. [F, H, RH], call(G), map(T, F, RT).

% From calc.pl
inc(N, R) :- number(N), !, R is N + 1.
inc(N, R) :- number(R), !, N is R - 1.

/*
?- map([10, 20, 30], inc, R).
   R / [11,21,31]
?- map(R, inc, [10, 20, 30]).
   R / [9,19,29]
*/


% Фильтрация

filter([], _, []).
filter([H | T], P, [H | RT]) :- G =.. [P, H], call(G), !, filter(T, P, RT).
filter([H | T], P, RT) :- filter(T, P, RT).

odd(N) :- 1 is mod(N, 2).

/*
?- filter([1, 2, 3], odd, R).
   R / [1,3]
*/


% Левая свертка

foldLeft([], Z, _, Z).
foldLeft([H | T], Z, F, R) :- G =.. [F, Z, H, RH], call(G), foldLeft(T, RH, F, R).

add(A, B, R) :- R is A + B.

/*
?- foldLeft([1, 2, 3], 0, add, R).
   R / 6
*/


% Транспонирование матрицы

transpose([[] | _], []) :- !.
transpose(M, [RH | RT]) :-
    map(M, head, RH),
    map(M, tail, Tails),
    transpose(Tails, RT).

head([H | _], H).
tail([_ | T], T).

/*
?- transpose([[1, 2], [3, 4]], R)
   R / [[1,3],[2,4]]
*/

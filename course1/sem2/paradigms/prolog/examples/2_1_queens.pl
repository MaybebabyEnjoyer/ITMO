%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Задача о расстановке ферзей %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% https://ru.wikipedia.org/wiki/Задача_о_восьми_ферзях

% Из lists.pl
range(L, L, []).
range(N, L, [N | T]) :- N < L, N1 is N + 1, range(N1, L, T).

zip([], _, []) :- !.
zip(_, [], []) :- !.
zip([H1 | T1], [H2 | T2], [(H1, H2) | RT]) :- zip(T1, T2, RT).

% Из high-order.pl
map([], _, []).
map([H | T], F, [RH | RT]) :- G =.. [F, H, RH], call(G), map(T, F, RT).


% Все элементы различны
distinct([]).
distinct([H | T]) :- distinct(T), \+ member(H, T).

% Все элементы первого списка входят во второй
all_members([], _).
all_members([H | T], L) :- member(H, L), all_members(T, L).

% Номер диагонали
diag1((R, C), D) :- D is R - C.
diag2((R, C), D) :- D is R + C.


% Наивное решение

queens(N, Rows) :-
    length(Rows, N),
    range(0, N, Range), all_members(Rows, Range),
    distinct(Rows),
    zip(Rows, Range, Queens),
    map(Queens, diag1, Diag1), distinct(Diag1),
    map(Queens, diag2, Diag2), distinct(Diag2).

/*
?- R = queens(4, R).
   R / [1,3,0,2]
   R / [2,0,3,1]
?- R = queens(7, R).
   R / [0,2,4,6,1,3,5]
*/


% Быстрое решение 
% (рекурсия по столбцам)

fast_queens(_, 0, [], [], []) :- !.
fast_queens(Range, C, [R | Rows], [D1 | Diag1], [D2 | Diag2]) :-
    C1 is C - 1,
    fast_queens(Range, C1, Rows, Diag1, Diag2),
    member(R, Range), \+ member(R, Rows),
    D1 is R - C, \+ member(D1, Diag1),
    D2 is R + C, \+ member(D2, Diag2).

fast_queens(N, R) :- range(0, N, Range), fast_queens(Range, N, R, _, _).

/*
?- R = fast_queens(8, R).
   R / [3,1,6,2,5,7,4,0]
   ... 91 more solutions
?- R = queens(10, R).
   R / [6,3,1,8,4,9,7,5,2,0]
   ... 723 more solutions
*/

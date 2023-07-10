:- load_library('alice.tuprolog.lib.DCGLibrary').

variable(Name, variable(Name)).
const(Value, const(Value)).

calculate(op_negate, A, R) :- R is -A.
calculate(op_not, A, 1) :- A =< 0, !.
calculate(op_not, A, 0).
calculate(op_add, A, B, R) :- R is A + B.
calculate(op_subtract, A, B, R) :- R is A - B.
calculate(op_multiply, A, B, R) :- R is A * B.
calculate(op_divide, A, B, R) :- R is A / B.
calculate(op_and, A, B, 1) :- A > 0, B > 0, !.
calculate(op_and, A, B, 0).
calculate(op_or, A, B, 0) :- A =< 0, B =< 0, !.
calculate(op_or, A, B, 1).
calculate(op_xor, A, B, 1) :- ((B > 0, A =< 0); (B =< 0, A > 0)), !.
calculate(op_xor, A, B, 0).
calculate(op_impl, A, B, 1) :- (B > 0; (B =< 0, A =< 0)), !.
calculate(op_impl, A, B, 0).
calculate(op_iff, A, B, 1) :- ((B > 0, A > 0); (B =< 0, A =< 0)), !.
calculate(op_iff, A, B, 0).

lookup(K, [(K, V) | _], V).
lookup(K, [_ | T], V) :- lookup(K, T, V).

evaluate(const(N), _, N).
evaluate(variable(Name), Vars, R) :- atom_chars(Name, [H | _]), lookup(H, Vars, R).
evaluate(operation(Op, A), Vars, R) :-
    evaluate(A, Vars, AV),
    calculate(Op, AV, R).
evaluate(operation(Op, A, B), Vars, R) :-
    evaluate(A, Vars, AV),
    evaluate(B, Vars, BV),
    calculate(Op, AV, BV, R).

nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

seq_xyz([]) --> [].
seq_xyz([C | T]) -->
  {member(C, ['x','y','z','X','Y','Z'])},
  [C], seq_xyz(T).

expr_p(variable(Name)) -->
    {nonvar(Name, atom_chars(Name, Chars))},
    seq_xyz(Chars),
    {not(empty(Chars)), atom_chars(Name, Chars)}.

digits_p([]) --> [].
digits_p([H | T]) -->
    { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'])},
    [H],
    digits_p(T).

expr_p(const(Value)) -->
    { nonvar(Value, number_chars(Value, Chars)) },
    digits_p(Chars),
    { Chars = [_ | _], \+ Chars = ['-'], number_chars(Value, Chars) }.

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_negate) --> ['n', 'e', 'g', 'a', 't', 'e'].
op_p(op_not) --> ['!'].
op_p(op_and) --> ['&', '&'].
op_p(op_or) --> ['|', '|'].
op_p(op_xor) --> ['^', '^'].
op_p(op_impl) --> ['-', '>'].
op_p(op_iff) --> ['<', '-', '>'].

opt_ws1(0) --> [].
opt_ws1(1) --> [' '].

opt_ws2(1) --> [' '].
opt_ws2(0) --> [].

expr_p(operation(Op, A, B)) -->
    ['('],
    opt_ws1(N1),
        expr_p(A),
    opt_ws2(N2),
        op_p(Op),
    opt_ws2(N3),
        expr_p(B),
    opt_ws1(N4),
    [')'].
expr_p(operation(Op, A)) --> op_p(Op), [' '], expr_p(A).
ws_expr_p(E) --> opt_ws1(N1), expr_p(E), opt_ws1(N2).

zip_ws([], []).
zip_ws([' ', ' ' | T], [' ' | R]) :- zip_ws([' ' | T], [' ' | R]), !.
zip_ws([C | T], [C | R]) :- zip_ws(T, R).

infix_str(E, A) :- ground(E), phrase(ws_expr_p(E), C), atom_chars(A, C), !.
infix_str(E, A) :- atom(A), atom_chars(A, C), zip_ws(C, D), phrase(ws_expr_p(E), D), !.
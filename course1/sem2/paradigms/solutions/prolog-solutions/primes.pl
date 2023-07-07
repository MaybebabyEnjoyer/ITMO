put_div(N, _) :- min_div(N, _), !.
put_div(N, I) :- assertz(min_div(N, I)).

mark_composites(I, J, N) :-
    J < N,
    assertz(composite(J)),
    put_div(J, I),
    J1 is J + I,
    mark_composites(I, J1, N).

handle_prime(I, N) :-
    \+ composite(I),
    J is I * I,
    mark_composites(I, J, N).

increment(I, N) :-
    I * I < N,
    I1 is I + 1,
    build_table(I1, N).

build_table(I, N) :-
    handle_prime(I, N);
    increment(I, N).

init(MAX_N) :-
    assertz(min_div(2, 2)),
    build_table(2, MAX_N);
    true.

is_min_div(P, P) :- prime(P), !.
is_min_div(N, P) :- min_div(N, P).

prime(A) :- \+ composite(A).

is_sorted([]).
is_sorted([_]).
is_sorted([X,Y|T]) :-
    X =< Y,
    is_sorted([Y|T]).

all_primes([]).
all_primes([H|T]) :-
    prime(H),
    all_primes(T).

is_valid_list(L) :-
    is_sorted(L),
    all_primes(L).

gen([], 1).

gen([H | T], R) :-
    gen(T, R1),
    R is R1 * H.

prime_factorization(1, []) :- !.

prime_factorization(N, [H | T]) :-
    number(N), !,
    is_min_div(N, H),
    N1 is N / H,
    prime_factorization(N1, T).

check_prime_divisors(N, L) :-
    is_valid_list(L),
    gen(L, N).

prime_divisors(N, L) :-
    number(N), !, prime_factorization(N, L);
    check_prime_divisors(N, L).

double_list([], []).
double_list([H|T], [H,H|D]) :-
    double_list(T, D).

square_divisors(N, D) :-
    prime_divisors(N, L),
    double_list(L, D).


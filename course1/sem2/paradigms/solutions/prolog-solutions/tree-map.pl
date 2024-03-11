create_node(Key, Value, Height, Balance, Left, Right, 
         tree(Key, Value, Height, Balance, Left, Right)).
create_node(Key, Value, Result) :-
   create_node(Key, Value, 1, 0, null, null, Result).

key(tree(Key, _, _, _, _, _), Key).
key(Tree, NewKey, NewTree) :-
    all(Tree, _, Value, Height, Balance, Left, Right), 
    create_node(NewKey, Value, Height, Balance, Left, Right, NewTree).
value(tree(_, Value, _, _, _, _), Value).
value(Tree, NewValue, NewTree) :-
    all(Tree, Key, _, Height, Balance, Left, Right), 
    create_node(Key, NewValue, Height, Balance, Left, Right, NewTree).
    
height(null, 0).
height(tree(_, _, Height, _, _, _), Height).
height(Tree, Height, NewTree) :-
    all(Tree, Key, Value, _, Balance, Left, Right), 
    create_node(Key, Value, Height, Balance, Left, Right, NewTree).

balance(null, 0).
balance(tree(_, _, _, Balance,_, _), Balance).
balance(Tree, Balance, NewTree) :-
    all(Tree, Key, Value, Height, _, Left, Right), 
    create_node(Key, Value, Height, Balance, Left, Right, NewTree).

right(tree(_, _, _, _, _, Right), Right).
right(Tree, NewRightTree, NewTree) :-
    all(Tree, Key, Value, Height, Balance, Left, _), 
    create_node(Key, Value, Height, Balance, Left, NewRightTree, NewTree).
left(tree(_, _, _, _, Left, _), Left).
left(Tree, NewLeftTree, NewTree) :-
    all(Tree, Key, Value, Height, Balance, _, Right), 
    create_node(Key, Value, Height, Balance, NewLeftTree, Right, NewTree).

all(tree(Key, Value, Height, Balance, Left, Right), 
    Key, Value, Height, Balance, Left, Right).

map_get(tree(Key, Value, _, _, _, _), Key, Value) :- !.
map_get(Tree, Key, Value) :- 
    key(Tree, KeyTree), Key > KeyTree, !, 
    right(Tree, RightTree), map_get(RightTree, Key, Value).
map_get(Tree, Key, Value) :- 
    key(Tree, KeyTree), Key < KeyTree, !, 
    left(Tree, LeftTree), map_get(LeftTree, Key, Value).  

max(F, S, F) :- F >= S, !.
max(F, S, S) :- S >= F, !.

set_height(Tree, NewTree) :-
    left(Tree, LeftTree), height(LeftTree, LeftTreeHeight),
    right(Tree, RightTree), height(RightTree, RightTreeHeight),
    max(LeftTreeHeight, RightTreeHeight, MaxHeight),
    NewHeight is 1 + MaxHeight,
    NewBalanse is LeftTreeHeight - RightTreeHeight,
    height(Tree, NewHeight, Curr),
    balance(Curr, NewBalanse, NewTree).

balance_node(Tree, Balance, _, RightBalance, NewTree) :-
    Balance = -2, RightBalance = 1, !,
    big_left_tern(Tree, NewTree).
balance_node(Tree, Balance, _, RightBalance, NewTree) :-
    Balance = -2, RightBalance \= 1, !,
    small_left_tern(Tree, NewTree).   
balance_node(Tree, Balance, LeftBalance, _, NewTree) :-
    Balance = 2, LeftBalance = -1, !,
    big_right_tern(Tree, NewTree).
balance_node(Tree, Balance, LeftBalance, _, NewTree) :-
    Balance = 2, LeftBalance \= -1, !,
    small_right_tern(Tree, NewTree).
balance_node(Tree, _, _, _, Tree).

balance_tree(null, null).
balance_tree(Tree, Result) :-
    set_height(Tree, CurrTree),
    balance(CurrTree, Balance), 
    left(CurrTree, LeftTree), balance(LeftTree, LeftBalance),
    right(CurrTree, RightTree), balance(RightTree, RightBalance),
    balance_node(CurrTree, Balance, LeftBalance, RightBalance, Result).

small_right_tern(El1, Result) :-
    left(El1, El2), right(El2, RightEl2),
    left(El1, RightEl2, T1), set_height(T1, Tree1),
    right(El2, Tree1, T2), set_height(T2, Result).
small_left_tern(El1, Result) :-
    right(El1, El2), left(El2, LeftEl2),
    right(El1, LeftEl2, T1), set_height(T1, Tree1),
    left(El2, Tree1, T2), set_height(T2, Result).
big_right_tern(El, Result) :-
    left(El, LeftEl), small_left_tern(LeftEl, Curr),
    left(El, Curr, T), 
    small_right_tern(T, Result).
big_left_tern(El, Result) :- 
    right(El, RightEl), small_right_tern(RightEl, Curr), 
    right(El, Curr, T), 
    small_left_tern(T, Result).
    
insert_key(null, _, Key, Value, Result) :-
    create_node(Key, Value, Result), !.
insert_key(Tree, 1, Key, Value, Result) :- 
    key(Tree, KeyTree), Key = KeyTree, !, 
    value(Tree, Value, Result).   
insert_key(Tree, 0, Key, _, Tree) :-
    key(Tree, KeyTree), Key = KeyTree, !.
insert_key(Tree, Flag, Key, Value, Result) :- 
    key(Tree, KeyTree), Key > KeyTree, !, 
    right(Tree, RightTree), insert_key(RightTree, Flag, Key, Value, NewRightTree),
    right(Tree, NewRightTree, CurrTree),
    balance_tree(CurrTree, Result).
insert_key(Tree, Flag, Key, Value, Result) :- 
    key(Tree, KeyTree), Key < KeyTree, !, 
    left(Tree, LeftTree), insert_key(LeftTree, Flag, Key, Value, NewLeftTree),
    left(Tree, NewLeftTree, CurrTree),
    balance_tree(CurrTree, Result).

map_put(Tree, Key, Value, Result) :-
    insert_key(Tree, 1, Key, Value, Result).
map_putIfAbsent(Tree, Key, Value, Result) :-
    insert_key(Tree, 0, Key, Value, Result).

get_pair([(K, V) | _], (K, V)).
get_tail([_ | Tail], Tail).

map_build([], null) :- !.
map_build(List, Tree) :-
    get_pair(List, (Key, Value)), get_tail(List, Tail),
    map_build(Tail, SmallTree), 
    map_put(SmallTree, Key, Value, Tree).
   
get_next_key(Tree, Tree) :-
    left(Tree, LeftTree), LeftTree = null, !.
get_next_key(Tree, Next) :-
    left(Tree, LeftTree), LeftTree \= null, !,
    get_next_key(LeftTree, Next).

delete_key(_, null, null, null) :- !.
delete_key(_, LeftTree, null, LeftTree) :- !.
delete_key(_, null, RightTree, RightTree) :- !.
delete_key(Tree, LeftTree, RightTree, NewTree) :-
    LeftTree \= null, RightTree \= null, !,
    get_next_key(RightTree, Next), 
    key(Next, Key), value(Next, Value),
    key(Tree, Key, T), value(T, Value, CurrTree),
    map_remove(RightTree, Key, Res), 
    right(CurrTree, Res, NewTree).
    
map_remove(null, _, null) :- !.
map_remove(Tree, Key, Result) :-
    key(Tree, KeyTree), Key = KeyTree, !,
    left(Tree, LeftTree), right(Tree, RightTree),
    delete_key(Tree, LeftTree, RightTree, CurrTree),
    balance_tree(CurrTree, Result).
map_remove(Tree, Key, Result) :-
    key(Tree, KeyTree), Key < KeyTree, !, 
    left(Tree, LeftTree), map_remove(LeftTree, Key, NewLeftTree),
    left(Tree, NewLeftTree, CurrTree),
    balance_tree(CurrTree, Result).
map_remove(Tree, Key, Result) :-
    key(Tree, KeyTree), Key > KeyTree, !, 
    right(Tree, RightTree), map_remove(RightTree, Key, NewRightTree),
    right(Tree, NewRightTree, CurrTree),
    balance_tree(CurrTree, Result).

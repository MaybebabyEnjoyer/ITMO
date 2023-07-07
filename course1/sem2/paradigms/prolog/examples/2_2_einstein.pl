%%%%%%%%%%%%%%%%%%%%%
% Загадка Эйнштейна %
%%%%%%%%%%%%%%%%%%%%%

% https://ru.wikipedia.org/wiki/Загадка_Эйнштейна

% Проверка что списки являются перестановкой друг друга
permutation([], _).
permutation([H | T], Values) :- member(H, Values), permutation(T, Values), \+ member(H, T).

% V1 в списке слева от V2
left(V1, V2, [V1, V2 | _]) :- !.
left(V1, V2, [H | T]) :- left(V1, V2, T).

% V1 в списке рядом с V2
next(V1, V2, R) :- left(V1, V2, R).
next(V1, V2, R) :- left(V2, V1, R).

solve(W, Z, R) :-
  R = [house(N1, C1, P1, D1, S1),               % На улице стоят пять домов.    
       house(N2, C2, P2, D2, S2),               % В каждом доме есть            
       house(N3, C3, P3, D3, S3),               %  - N: национальность жителя      
       house(N4, C4, P4, D4, S4),               %  - C: цвет дома                  
       house(N5, C5, P5, D5, S5)],              %  - P: питомец                    
                                                %  - D: любимый напиток            
                                                %  - S: любимый тип сигарет        
  D3 = mlk,                                     % В центральном доме пьют молоко.
  N1 = nrg,                                     % Норвежец живёт в первом доме.
  member(house(eng, red, _  ,   _, _  ), R),    % Англичанин живёт в красном доме.
  member(house(spn, _  , dog,   _, _  ), R),    % У испанца есть собака.
  member(house(_  , grn, _  , cof, _  ), R),    % В зелёном доме пьют кофе.
  member(house(ukr, _  , _  , tea, _  ), R),    % Украинец пьёт чай.
  member(house(_  , _  , slu, _  , old), R),    % Тот, кто курит Old Gold, разводит улиток.
  member(house(_  , yel, _  , _  , koo), R),    % В жёлтом доме курят Kool.
  member(house(_  , _  , _  , ora, luk), R),    % Тот, кто курит Lucky Strike, пьёт апельсиновый сок.
  member(house(jap, _  , _  , _  , par), R),    % Японец курит Parliament.
  left(house(_  , whi, _  , _  , _  ),          % Зелёный дом стоит сразу справа от белого дома.
       house(_  , grn, _  , _  , _  ), R),      
  next(house(_  , _  , _  , _  , che),          % Сосед того, кто курит Chesterfield, держит лису.
       house(_  , _  , fox, _  , _  ), R),      
  next(house(_  , _  , hrs, _  , _  ),          % В доме по соседству с тем, в котором держат лошадь, курят Kool.
       house(_  , _  , _  , _  , koo), R),      
  next(house(nrg, _  , _  , _  , _  ),          % Норвежец живёт рядом с синим домом.
       house(_  , blu, _  , _  , _  ), R),      
  permutation([N1, N2, N3, N4, N5], [eng, spn, ukr, jap, nrg]),
  permutation([C1, C2, C3, C4, C5], [red, grn, yel, whi, blu]),
  permutation([P1, P2, P3, P4, P5], [dog, slu, fox, hrs, zeb]),
  permutation([D1, D2, D3, D4, D5], [mlk, cof, tea, ora, wat]),
  permutation([S1, S2, S3, S4, S5], [old, koo, luk, par, che]),
  member(house(W  , _  , _  ,  wat, _  ), R),   % Кто пьет воду?
  member(house(Z  , _  , zeb,  _  , _  ), R).   % Кто держит зебру?

/*
?- solve(W, Z, R).
   W / nrg  
   Z / jap  
   R / [house(nrg,yel,fox,wat,koo),
        house(ukr,blu,hrs,tea,che),
        house(eng,red,slu,mlk,old),
        house(spn,whi,dog,ora,luk),
        house(jap,grn,zeb,cof,par)]
*/

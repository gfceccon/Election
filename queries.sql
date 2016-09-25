--1.a
--Pega as tabelas do usuário
SELECT TABLE_NAME AS NAME FROM USER_TABLES
  MINUS SELECT MVIEW_NAME AS NAME FROM USERS_MVIEWS;
--Pega as views do usuário
SELECT VIEW_NAME AS NAME FROM USER_VIEWS;
--Pega as materialized views do usuário
SELECT MVIEW_NAME AS NAME FROM USER_MVIEWS;

--1.c
SELECT table_name, column_name, data_type, data_length
FROM USER_TAB_COLUMNS WHERE table_name = 'MYTABLE';
SELECT * FROM TABLE_NAME;

--1.d
--Pegar todos os CHECKs de um dado
--Retorna algo do tipo: Esfera IN ('F','E','M')
--Checkar string, se tiver IN splita nele, pega nome da coluna a esquerda, da replace em () e '' e splita na ,
--pegando assim as constraints
select search_condition from user_constraints where table_name = 'TABLE_NAME' and constraint_type = 'C';

--1.e
SELECT a.column_name as COLUNA_REFERENCIADA,
  a.table_name AS TABELA_REFERENCIADA,
  b.column_name AS COLUNA_ORIGEM,
  u.table_name AS TABELA_ORIGEM
  FROM user_constraints u
  JOIN all_cons_columns a ON a.constraint_name = u.r_constraint_name
  JOIN all_cons_columns b ON b.constraint_name = u.constraint_name
  WHERE u.table_name='LE02CIDADE' AND u.constraint_type='R';

--1.f
select listagg(privilege, ',') within group( order by privilege) as PRIVILEGE, grantee
from USER_TAB_PRIVS where table_name='LE08CANDIDATO' GROUP BY GRANTEE;

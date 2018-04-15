/*简单的树形结构*/
select empno as 员工编码,
       ename as 姓名,
       mgr as 主管编码,
       (prior ename) as 主管姓名
  from emp
 start with empno = 7566
connect by (prior empno) = mgr;
/*根节点、分支节点、叶子节点*/
select lpad('-', (level - 1) * 2, '-') || empno as 员工编码,
       ename as 姓名,
       mgr as 主管编码,
       level as 级别,
       decode(level, 1, 1) as 根节点,
       decode(connect_by_isleaf, 1, 1) as 叶子节点,
       case
         when (connect_by_isleaf = 0 and level > 1) then
          1
       end as 分支节点
  from emp
 start with empno = 7566
connect by (prior empno) = mgr;
/*sys_connect_by_path*/
select empno as 员工编码,
       ename as 姓名,
       mgr as 主管编码,
       sys_connect_by_path(ename, ',') as enames
  from emp
 start with empno = 7566
connect by (prior empno) = mgr;

with x1 as
/*1.分组生成序号rn*/
 (select deptno,
         ename,
         row_number() over(partition by deptno order by ename) as rn
    from emp)
/*2.用sys_connect_by_path合并字符串*/
select deptno, sys_connect_by_path(ename, ',') as emps
  from x1
 where connect_by_isleaf = 1
 start with rn = 1
connect by (prior deptno) = deptno
       and (prior rn) = rn - 1;
/*树形查询中的排序*/
select lpad('-', (level - 1) * 2, '-') || empno as 员工编码,
       ename as 姓名,
       mgr as 主管编码
  from emp
 start with empno = 7566
connect by (prior empno) = mgr
 order by empno desc;

select lpad('-', (level - 1) * 2, '-') || empno as 员工编码,
       ename as 姓名,
       mgr as 主管编码
  from emp
 start with empno = 7566
connect by (prior empno) = mgr
 order siblings by empno desc;
/*树形查询中的WHERE*/
select empno  as 员工编码,
       mgr    as 主管编码,
       ename  as 姓名,
       deptno as 部门编码
  from emp
 where deptno = 20
 start with mgr is null
connect by (prior empno) = mgr;

select empno  as 员工编码,
       mgr    as 主管编码,
       ename  as 姓名,
       deptno as 部门编码
  from (select * from emp where deptno = 20) emp
 start with mgr is null
connect by (prior empno) = mgr;
/*查询树形的一个分支*/
select empno as 员工编码, mgr as 主管编码, ename as 姓名, level as 级别
  from emp
 start with empno = 7698
connect by (prior empno) = mgr;
/*剪去一个分支*/
select empno as 员工编码, mgr as 主管编码, ename as 姓名, level as 级别
  from emp
 start with mgr is null
connect by (prior empno) = mgr
          /*剪去分支*/
       and empno != 7698;
/*字段内list值去重*/
create or replace view v as
select 1 as id, 'ab,b,c,ab,d,e' as a1
  from dual
union all
select 2 as id, '11,2,3,4,11,2'
  from dual;

create or replace view v2 as
select 1 as id, 'ab,b,c,ab,d,e' as a1 from dual;

select regexp_substr(a1, '[^,]+', 1, level) as a2
  from v2
connect by level <= regexp_count(a1, ',') + 1;

select count(*)
  from (select regexp_substr(a1, '[^,]+', 1, level) as a2
          from v
        connect by level <= regexp_count(a1, ',') + 1);

select b.*, power(4, lv - 1) as pw
  from (select level as lv,
               a1,
               length(a1) as l,
               sys_connect_by_path(id, '-') as p,
               count(*) over(partition by a1, level) as ct
          from v
        connect by level <= regexp_count(a1, ',') + 1) b
 order by 2, 1;

select regexp_substr(a1, '[^,]+', 1, level) as a2
  from v
connect by (level <= regexp_count(a1, ',') + 1and(prior id) = id);

select count(*)
  from (select a1,
               level as lv,
               regexp_substr(a1, '[^,]+', 1, level) as a2,
               'regexp_substr(''' || a1 || ''',''[^,]+'',1,' || level || ')' as fun
          from v
        connect by nocycle(level <= regexp_count(a1, ',') + 1
                       and (prior id) = id
                       and (prior dbms_random.value()) is not null));

with v2 as
 (select a1,
         level as lv,
         regexp_substr(a1, '[^,]+', 1, level) as a2,
         'regexp_substr(''' || a1 || ''',''[^,]+'',1,' || level || ')' as fun
    from v
  connect by nocycle(level <= regexp_count(a1, ',') + 1
                 and (prior id) = id
                 and (prior dbms_random.value()) is not null))
select v3.a1 as old_a1,
       listagg(v3.a2, ',') within group(order by v3.a2) as new_a1
  from (select a1, a2 from v2 group by a1, a2) v3
 group by v3.a1
 order by 1;

/*UNION ALL与空字符串*/
select empno as 编码, ename as 名称, nvl(mgr, deptno) as 上级编码
  from emp
 where empno = 7788
union all
select deptno as 编码, dname as 名称, null as 上级编码
  from dept
 where deptno = 10;
/*UNION与OR*/
create index idx_emp_empno on emp(empno);
create index idx_emp_empno on emp(ename);

select empno, ename
  from emp
 where empno = 7788
union
select empno, ename
  from emp
 where ename = 'SCOTT';
/*组合相关的行*/
select e.empno, e.ename, d.loc
  from emp e
 inner join dept d
    on (e.deptno = d.deptno)
 where e.deptno = 10;

select e.empno, e.ename, d.loc
  from emp e, dept d
 where e.deptno = d.deptno
   and e.deptno = 10;
/*IN、EXISTS和INNER JOIN*/
create table emp2 as
  select ename, job, sal, comm from emp where job = 'CLERK';

explain plan for
  select empno, ename, job, sal, deptno
    from emp
   where (ename, job, sal) in (select ename, job, sal from emp2);
select * from table(dbms_xplan.display());

explain plan for
  select empno, ename, job, sal, deptno
    from emp a
   where exists (select null
            from emp2 b
           where b.ename = a.ename
             and b.job = a.job
             and b.sal = a.sal);
select * from table(dbms_xplan.display());

explain plan for
  select a.empno, a.ename, a.job, a.sal, a.deptno
    from emp a
   inner join emp2 b
      on (b.ename = a.ename and b.job = a.job and b.sal = a.sal);
select * from table(dbms_xplan.display());
/*INNER JOIN、LEFT JOIN、RIGHT JOIN和FULL JOIN解析*/
/*左表*/
create table L as
  select 'left_1' as str, '1' as v
    from dual
  union all
  select 'left_2', '2' as v
    from dual
  union all
  select 'left_3', '3' as v
    from dual
  union all
  select 'left_4', '4' as v
    from dual;
/*右表*/
create table R as
  select 'right_3' as str, '3' as v, 1 as status
    from dual
  union all
  select 'right_4' as str, '4' as v, 0 as status
    from dual
  union all
  select 'right_5' as str, '5' as v, 0 as status
    from dual
  union all
  select 'right_6' as str, '6' as v, 0 as status
    from dual;

select l.str as left_str, r.str as right_str
  from l
 inner join r
    on l.v = r.v
 order by 1, 2;
select l.str as left_str, r.str as right_str
  from l, r
 where l.v = r.v
 order by 1, 2;

select l.str as left_str, r.str as right_str
  from l
  left join r
    on l.v = r.v
 order by 1, 2;
select l.str as left_str, r.str as right_str
  from l, r
 where l.v = r.v(+)
 order by 1, 2;

select l.str as left_str, r.str as right_str
  from l
 right join r
    on l.v = r.v
 order by 1, 2;
select l.str as left_str, r.str as right_str
  from l, r
 where l.v(+) = r.v
 order by 1, 2;

select l.str as left_str, r.str as right_str
  from l
  full join r
    on l.v = r.v
 order by 1, 2;
/*自关联*/
select 员工.empno as 职工编码,
       员工.ename as 职工姓名,
       员工.job   as 工作,
       员工.mgr   as 员工表_主管编码,
       主管.empno as 主管表_主管编码,
       主管.ename as 主管姓名
  from emp 员工
  left join emp 主管
    on (员工.mgr = 主管.empno)
 order by 1;
/*NOT IN、NOT EXISTS和LEFT JOIN*/
explain plan for
  select *
    from dept
   where deptno not in
         (select emp.deptno from emp where emp.deptno is not null);
select * from table(dbms_xplan.display());

explain plan for
  select *
    from dept
   where not exists (select null from emp where emp.deptno = dept.deptno);
select * from table(dbms_xplan.display());

explain plan for
  select dept.*
    from dept
    left join emp
      on emp.deptno = dept.deptno
   where emp.deptno is null;
select * from table(dbms_xplan.display());
/*外连接中的条件不要乱放*/
select l.str as left_str, r.str as right_str, r.status
  from l
  left join r
    on l.v = r.v
 where r.status = 1
 order by 1, 2;

select l.str as left_str, r.str as right_str, r.status
  from l
  left join r
    on (l.v = r.v and r.status = 1)
 order by 1, 2;

select l.str as left_str, r.str as right_str, r.status
  from l
  left join (select * from r where r.status = 1) r
    on (l.v = r.v and r.status = 1)
 order by 1, 2;
select * from table(dbms_xplan.display);
/*检测两个表中的数据及对应数据的条数是否相同*/
create or replace view v as
  select *
    from emp
   where deptno != 10
  union all
  select *
    from emp
   where ename = 'SCOTT';

select rownum, empno, ename from v where ename = 'SCOTT';
select rownum, empno, ename from emp where ename = 'SCOTT';

select v.empno, v.ename, b.empno, b.ename
  from v
  full join emp b
    on (b.empno = v.empno)
 where (v.empno is null or b.empno is null);

select v.empno, v.ename, v.cnt, emp.empno, emp.ename, emp.cnt
  from (select empno, ename, count(*) as cnt from v group by empno, ename) v
  full join (select empno, ename, count(*) as cnt
               from emp
              group by empno, ename) emp
    on (emp.empno = v.empno and emp.cnt = v.cnt)
 where (v.empno is null or emp.empno is null);
/*聚集与内连接*/
create table emp_bonus(empno INT, received date, type INT);
insert into emp_bonus values (7934, date '2005-5-17', 1);
insert into emp_bonus values (7934, date '2005-2-15', 2);
insert into emp_bonus values (7839, date '2005-2-15', 3);
insert into emp_bonus values (7782, date '2005-2-15', 1);

select e.deptno,
       e.empno,
       e.ename,
       e.sal,
       (e.sal * case
         when eb.type = 1 then
          0.1
         when eb.type = 2 then
          0.2
         when eb.type = 3 then
          0.3
       end) as bonus
  from emp e
 inner join emp_bonus eb
    on (e.empno = eb.empno)
 where e.deptno = 10
 order by 1, 2;

select e.deptno,
       sum(e.sal) as total_sal,
       sum(e.sal * case
             when eb.type = 1 then
              0.1
             when eb.type = 2 then
              0.2
             when eb.type = 3 then
              0.3
           end) as total_bonus
  from emp e
 inner join emp_bonus eb
    on (e.empno = eb.empno)
 where e.deptno = 10
 group by e.deptno;

select sum(sal) as total_sal from emp where deptno = 10;

select eb.empno,
       (case
         when eb.type = 1 then
          0.1
         when eb.type = 2 then
          0.2
         when eb.type = 3 then
          0.3
       end) as rate
  from emp_bonus eb
 order by 1, 2;

select eb.empno,
       sum(case
             when eb.type = 1 then
              0.1
             when eb.type = 2 then
              0.2
             when eb.type = 3 then
              0.3
           end) as rate
  from emp_bonus eb
 group by empno
 order by 1, 2;

select e.deptno,
       sum(e.sal) as total_sal,
       sum(e.sal * eb2.rate) as total_bonus
  from emp e
 inner join (select eb.empno,
                    sum(case
                          when eb.type = 1 then
                           0.1
                          when eb.type = 2 then
                           0.2
                          when eb.type = 3 then
                           0.3
                        end) as rate
               from emp_bonus eb
              group by eb.empno) eb2
    on eb2.empno = e.empno
 where e.deptno = 10
 group by e.deptno;
/*聚集与外连接*/
select e.deptno,
       sum(e.sal) as total_sal,
       sum(e.sal * eb2.rate) as total_bonus
  from emp e
  left join (select eb.empno,
                    sum(case
                          when eb.type = 1 then
                           0.1
                          when eb.type = 2 then
                           0.2
                          when eb.type = 3 then
                           0.3
                        end) as rate
               from emp_bonus eb
              group by eb.empno) eb2
    on eb2.empno = e.empno
 group by e.deptno
 order by 1;
/*从多个表中返回丢失的数据*/
select emp.empno, emp.ename, dept.deptno, dept.dname
  from emp
 inner join dept
    on dept.deptno = emp.deptno;

select emp.empno, emp.ename, dept.deptno, dept.dname
  from emp
  full join dept
    on dept.deptno = emp.deptno;

select emp.empno, emp.ename, dept.deptno, dept.dname
  from emp
  left join dept
    on dept.deptno = emp.deptno
union all
select emp.empno, emp.ename, dept.deptno, dept.dname
  from emp
 right join dept
    on dept.deptno = emp.deptno
 where emp.empno is null;
/*多表查询时的空值处理*/
select a.ename, a.comm
  from emp a
 where a.comm < (select b.comm from emp b where b.ename = 'ALLEN');

select a.ename, a.comm
  from emp a
 where coalesce(a.comm, 0) <
       (select b.comm from emp b where b.ename = 'ALLEN');

select *
  from dept
 where deptno not in
       (select emp.deptno from emp where emp.deptno is not null);

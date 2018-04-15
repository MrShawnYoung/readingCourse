/*查询表中所有行与列*/
select * from emp;
/*从表中检索部分行*/
select * from emp where job = 'SALESMAN';
/*查找空值*/
select * from emp where comm is null;
/*将空值转换为实际值，coalesce支持多个参数*/
select coalesce(comm, 0) from emp;
/*查找满足多个条件的行*/
select *
  from emp
 where (deptno = 10 or comm is not null or (sal <= 2000 and deptno = 20));
/*从表中检索部分列*/
select empno, ename, hiredate, sal from emp where deptno = 10;
/*为列取有意义的名称*/
select ename as 姓名, deptno as 部门编号, sal as 工资, comm as 提成
  from emp;
/*在where子句中引用取别名的列*/
select *
  from (select sal as 工资, comm as 提成 from emp) x
 where 工资 < 1000;
/*拼接列*/
select ename || '的工作是' || job as msg from emp where deptno = 10;
/*在select语句中使用条件逻辑*/
select ename,
       sal,
       case
         when sal <= 2000 then
          '过低'
         when sal >= 4000 then
          '过高'
         else
          'OK'
       end as status
  from emp
 where deptno = 10;
/*限制返回的行数*/
select * from emp where rownum <= 2;
/*从表中随机返回n条记录*/
select empno, ename
  from (select empno, ename from emp order by dbms_random.value())
 where rownum <= 3;
/*模糊查询*/
create or replace view v as
  select 'ABCEDF' as vname
    from dual
  union all
  select '_BCEFG' as vname
    from dual
  union all
  select '_BCEDF' as vname
    from dual
  union all
  select '_\BCEDF' as vname
    from dual
  union all
  select 'XYCEG' as vname
    from dual;
/*escape把'\'标识为转义字符*/
select * from v where vname like '%CED%';
select * from v where vname like '\_BCE%' escape '\';
select * from v where vname like '_\\BCE%' escape '\';

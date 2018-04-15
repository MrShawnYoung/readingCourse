/*以指定的次序返回查询结果*/
select empno, ename, hiredate
  from emp
 where deptno = 10
 order by hiredate asc;
/*按第三列排序*/
select empno, ename, hiredate from emp where deptno = 10 order by 3 asc;
/*按多个字段排序*/
select empno, deptno, sal, ename, job from emp order by 2 asc, 3 desc;
/*按子串排序(表不存在，只展示语句)*/
select last_name as 名称,
       phone_number as 号码,
       salary as 工资,
       substr(phone_number, -4) as 尾号
  from hr.employees
 where rownum <= 5
 order by 4;
/*TRANSLATE*/
select translate('ab 你好 bcadefg', 'abcdefg', '1234567') as NEW_STR
  from dual;
/*按数字和字母混合字符串中的字母排序*/
create or replace view V as
  select empno || ' ' || ename as data from emp;
select * from v;
select data, translate(data, '- 0123456789', '-') as ename
  from v
 order by 2;
/*处理排序空值*/
select ename, sal, comm, nvl(comm, -1) order_col from emp order by 4;
/*在前*/
select ename, sal, comm from emp order by 3 nulls first;
/*在后*/
select ename, sal, comm from emp order by 3 nulls last;
/*根据条件取不同列中的值来排序*/
select empno as 编码,
       ename as 姓名,
       case
         when sal >= 1000 and sal < 2000 then
          1
         else
          2
       end as 级别,
       sal as 工资
  from emp
 where deptno = 30
 order by 3, 4;

select empno as 编码, ename as 姓名, sal as 工资
  from emp
 where deptno = 30
 order by case
            when sal >= 1000 and sal < 2000 then
             1
            else
             2
          end,
          3;

/*常用聚合函数*/
select deptno,
       avg(sal) as 平均值,
       min(sal) as 最小值,
       max(sal) as 最大值,
       sum(sal) 工资合计,
       count(*) 总行数,
       count(comm) 获得提成的人数,
       avg(comm) 错误的人均提成算法,
       avg(coalesce(comm, 0)) 正确的人均提成 /*需要把空值转换为0*/
  from emp
 group by deptno;

create table emp2 as select * from emp where 1=2;

select count(*) as cnt, sum(sal) as sum_sal from emp2 where deptno = 10;

select count(*) as cnt, sum(sal) as sum_sal
  from emp2
 where deptno = 10
 group by deptno;

declare
  v_sal emp2.sal%type;
begin
  select sum(sal) into v_sal from emp2 where deptno = 10;
  dbms_output.put_line('v_sal=' || v_sal);
end;
/*报错*/
declare
  v_sal emp2.sal%type;
begin
  select sum(sal) into v_sal from emp2 where deptno = 10 group by deptno;
  dbms_output.put_line('v_sal=' || v_sal);
end;
/*生成累计和*/
select empno as 编号,
       ename as 姓名,
       sal as 人工成本,
       sum(sal) over(order by empno) as 成本累计
  from emp
 where deptno = 30
 order by empno;

select * from table(dbms_xplan.display_cursor(null, 0, 'ALL-NOTE-ALIAS'));

select empno as 编号,
       ename as 姓名,
       sal as 人工成本,
       sum(sal) over(order by empno) as 成本累计,
       (select listagg(sal, '+') within group(order by empno)
          from emp b
         where b.deptno = 30
           and b.empno <= a.empno) 计算公式
  from emp a
 where deptno = 30
 order by empno;

select empno,
       sal,
       sum(sal) over(order by empno) as 简写,
       sum(sal) over(order by empno rows between unbounded preceding and current row) as row开窗,
       sum(sal) over(order by empno range between unbounded preceding and current row) as range开窗,
       (select sum(sal) from emp b where b.empno <= a.empno) as 标量,
       '(select sum(sal) from emp b where b.empno <=)' || a.empno || ')' as 标量解释
  from emp a
 where deptno = 30
 order by 1;

select empno as 编号,
       ename as 姓名,
       sal as 人工成本,
       sum(sal) over(order by empno) as 成本累计
  from emp
 where deptno = 30
 order by ename;
/*计算累计差*/
create table detail as
select 1000 as 编号, '预交费用' as 项目, 30000 as 金额 from dual;

insert into detail
  select empno as 编号, '支出' || rownum as 项目, sal + 1000 as 金额
    from emp
   where deptno = 10;

select rownum as seq, a.*
  from (select 编号, 项目, 金额 from detail order by 编号) a;

with x as
 (select rownum as seq, a.*
    from (select 编号, 项目, 金额 from detail order by 编号) a)
select 编号,
       项目,
       金额,
       (case
         when seq = 1 then
          金额
         else
          -金额
       end) as 转换后的值
  from x;

with x as
 (select rownum as seq, a.*
    from (select 编号, 项目, 金额 from detail order by 编号) a)
select 编号,
       项目,
       金额,
       sum(case
             when seq = 1 then
              金额
             else
              -金额
           end) over(order by seq) as 转换后的值
  from x;
/*更改累计和的值*/
create or replace view v(id,amt,trx)
as
select 1, 100, 'PR'
  from dual
union all
select 2, 100, 'PR'
  from dual
union all
select 3, 50, 'PY'
  from dual
union all
select 4, 100, 'PR'
  from dual
union all
select 5, 200, 'PY'
  from dual
union all
select 6, 50, 'PY'
  from dual;

select id,
       case
         when trx = 'PY' then
          '取款'
         else
          '存款'
       end 存取类型,
       amt 金额,
       (case
         when trx = 'PY' then
          -amt
         else
          amt
       end) as 变更后的值
  from v
 order by id;

select id,
       case
         when trx = 'PY' then
          '取款'
         else
          '存款'
       end 存取类型,
       amt 金额,
       sum(case
             when trx = 'PY' then
              -amt
             else
              amt
           end) over(order by id) as 余额
  from v
 order by id;
/*返回各部门工资排名前三位的员工*/
select deptno,
       empno,
       sal,
       row_number() over(partition by deptno order by sal desc) as row_number,
       rank() over(partition by deptno order by sal desc) as rank,
       dense_rank() over(partition by deptno order by sal desc) as dense_rank
  from emp
 where deptno in (20, 30)
 order by 1, 3 desc;

select *
  from (select deptno,
               empno,
               sal,
               dense_rank() over(partition by deptno order by sal desc) as dense_rank
          from emp
         where deptno in (20, 30))
 where dense_rank <= 3;
/*计算出次数最多的值*/
select sal, count(*) as 出现次数 from emp where deptno = 20 group by sal;

select sal, dense_rank() over(order by 出现次数 desc) as 次数排序
  from (select sal, count(*) as 出现次数
          from emp
         where deptno = 20
         group by sal) x;

select deptno, sal
  from (select deptno,
               sal,
               dense_rank() over(partition by deptno order by 出现次数 desc) as 次数排序
          from (select sal, deptno, count(*) as 出现次数
                  from emp
                 group by deptno, sal) x) y
 where 次数排序 = 1;
/*返回最值所在行数据*/
select deptno,
       empno,
       (select max(b.ename) from emp b where b.sal = a.max_sal) as 工资最高的人,
       ename,
       sal
  from (select deptno,
               empno,
               max(sal) over(partition by deptno) as max_sal,
               ename,
               sal
          from emp a
         where deptno = 10) a
 order by 1, 5 desc;

select deptno,
       empno,
       max(ename) keep(dense_rank first order by sal) over(partition by deptno) as 工资最低的人,
       max(ename) keep(dense_rank last order by sal) over(partition by deptno) as 工资最高的人,
       ename,
       sal
  from emp
 where deptno = 10
 order by 1, 6 desc;

select deptno,
       min(sal) as min_sal,
       max(ename) keep(dense_rank first order by sal) as 工资最低的人,
       max(sal) as max_sal,
       max(ename) keep(dense_rank last order by sal) as 工资最高的人
  from emp
 where deptno = 10
 group by deptno;

select deptno,
       empno,
       max(sal) over(partition by deptno) as 最高工资,
       ename,
       sal
  from emp
 where deptno = 20
 order by 1, 5 desc;

select deptno,
       empno,
       ename,
       sal,
       to_char(wmsys.wm_concat(ename) keep(dense_rank last order by sal)
               over(partition by deptno)) as 工资最高的人,
       min(ename) keep(dense_rank last order by sal) over(partition by deptno) as 工资最高的人min,
       max(ename) keep(dense_rank last order by sal) over(partition by deptno) as 工资最高的人max
  from emp
 where deptno = 20
 order by 1, 4 desc;
/*fisrt_value*/
select deptno,
       empno,
       first_value(ename) over(partition by deptno order by sal desc) as 工资最高的人,
       ename,
       sal
  from emp
 where deptno = 10
 order by 1, 5 desc;

select deptno,
       empno,
       last_value(ename) over(partition by deptno order by sal desc) as 工资最高的人,
       ename,
       sal
  from emp
 where deptno = 10
 order by 1, 5;

select deptno,
       empno,
       min(sal) over(partition by deptno order by sal desc) as 最高工资,
       ename,
       sal
  from emp
 where deptno = 10
 order by 1, 5;

select deptno,
       empno,
       max(sal) over(partition by deptno order by sal) as 最高工资,
       ename,
       sal
  from emp
 where deptno = 10
 order by 1, 5;

select deptno,
       empno,
       ename,
       sal,
       first_value(ename) over(partition by deptno order by sal desc, ename) as 工资最高的人min,
       first_value(ename) over(partition by deptno order by sal desc, ename) as 工资最高的人max
  from emp
 where deptno = 20
 order by 1, 4 desc;
/*求总和的百分比*/
select deptno, sum(sal) 工资总计 from emp group by deptno;

select deptno, 工资总计, sum(工资总计) over() as 总合计
  from (select deptno, sum(sal) 工资总计 from emp group by deptno) x;

select deptno as 部门,
       工资总计,
       round((工资总计 / 总合计) * 100, 2) as 工资比例
  from (select deptno, 工资总计, sum(工资总计) over() as 总合计
          from (select deptno, sum(sal) 工资总计 from emp group by deptno) x) y
 order by 1;

select deptno, round(ratio_to_report(工资合计) over() * 100, 2) as 工资比例
  from (select deptno, sum(sal) 工资合计 from emp group by deptno)
 order by 1;

select deptno,
       empno,
       ename,
       sal,
       round(ratio_to_report(sal) over(partition by deptno) * 100, 2) as 工资比例
  from emp
 order by 1, 2;

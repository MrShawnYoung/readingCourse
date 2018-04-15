/*给结果集分页*/
select rn as 序号, ename as 姓名, sal as 工资
/*3、根据前面生成的序号过滤掉6行以前的数据*/
  from (select rownum as rn, sal, ename
        /*2、取得排序后的序号，并过滤掉10行以后的数据*/
          from (
                /*1、按sal排序*/
                select sal, ename from emp where sal is not null order by sal) x
         where rownum <= 10)
 where rn >= 6;

select rownum as e2_seq, e1_seq, sal, ename
  from (select rownum as e1_seq, sal, ename
          from emp e1
         where sal is not null
           and deptno = 20
         order by sal) e2;

select count(*) from emp;

select count(*)
  from emp
 where rownum >= 6
   and rownum <= 10;

select rn as 序号, ename as 姓名, sal as 工资
  from (select row_number() over(order by sal) as rn, sal, ename
          from emp
         where sal is not null) x
 where rn between 6 and 10;
/*重新生成房间号*/
create table hotel(floor_nbr,room_nbr) as
select 1, 100
  from dual
union all
select 1, 100
  from dual
union all
select 2, 100
  from dual
union all
select 2, 100
  from dual
union all
select 3, 100
  from dual;
/*错误演示1*/
update hotel
   set room_nbr =
       (floor_nbr * 100) + row_number()
       over(partition by floor_nbr order by rowid);
/*错误演示2*/
update (select rowid,
               room_nbr,
               (floor_nbr * 100) + row_number() over(partition by floor_nbr order by rowid) as new_nbr
          from hotel)
   set room_nbr = new_nbr;
/*效率低*/
update hotel a
   set room_nbr =
       (select room_nbr
          from (select (floor_nbr * 100) + row_number() over(partition by floor_nbr order by rowid) as room_nbr
                  from hotel) b
         where a.rowid = b.rowid);
select * from hotel;
/*效率高*/
merge into hotel a
using (select rowid as rid,
              (floor_nbr * 100) + row_number() over(partition by floor_nbr order by rowid) as room_nbr
         from hotel) b
on (a.rowid = b.rowid)
when matched then
  update set a.room_nbr = b.room_nbr;
/*跳过表中n行*/
select empno, ename, sal, mod(rn, 2) as m
  from (select rownum as rn, empno, ename, sal
          from (select empno, ename, sal from emp order by ename) x) y;

select empno, ename, sal, mod(rn, 2) as m
  from (select rownum as rn, empno, ename, sal
          from (select empno, ename, sal from emp order by ename) x) y
 where mod(rn, 2) = 1;
/*排列组合去重*/
create table test(id,t1,t2,t3)as
select 1, '1', '3', '2'
  from dual
union all
select 2, '1', '3', '2'
  from dual
union all
select 3, '3', '2', '1'
  from dual
union all
select 4, '4', '2', '1'
  from dual;

select * from test unpivot(b2 for b3 in(t1, t2, t3));

with x1 as
 (select * from test unpivot(b2 for b3 in(t1, t2, t3)))
select id, listagg(b2, ',') within group(order by b2) as b
  from x1
 group by id;

with x1 as
 (select * from test unpivot(b2 for b3 in(t1, t2, t3))),
x2 as
 (select id, listagg(b2, ',') within group(order by b2) as b
    from x1
   group by id)
select id, b, row_number() over(partition by b order by id) as sn from x2;
/*找到包含最大值和最小值的记录*/
create test test as select * from dba_objects;
create index idx_test_object_id on test(object_id);
begin dbms_stats.get_table_stats(ownname => user, tabname => 'test');

select object_name, object_id
  from test
 where object_id in (select min(object_id)
                       from test
                     union all
                     select max(object_id)
                       from test);

select object_name, object_id
  from (select object_name,
               object_id,
               min(object_id) min_id,
               max(object_id) max_id
          from test) x
 where object_id in (min_id, max_id);

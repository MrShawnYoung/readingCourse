/*插入新记录*/
create table test(
c1 varchar2(10) default '默认1',
c2 varchar2(10) default '默认2',
c3 varchar2(10) default '默认3',
c4 date default sysdate
);
insert into test (c1, c2, c3) values (default, null, '手输值');
select * from test;
/*阻止对某几列插入*/
create or replace view v_test as select c1, c2, c3 from test;
insert into v_test (c1, c2, c3) values ('手输c1', null, '不能改c4');
select * from V_TEST;
/*insert into v_test (c1, c2, c3) values (default, null, '不能改c4');*/
/*复制表的定义及数据*/
create table test2 as select * from test;
create table test2 as select * from test where 1 = 2;
insert into test2 select * from test;
select * from test2;
/*用WITH CHECK OPTION限制数据录入*/
alter table emp add constraints ch_sal check(sal > 0);
/*alter table emp add constraints ch_hiredate check(hiredate > sysdate);*/
insert into
  (select empno, ename, hiredate
     from emp
    where hiredate <= sysdate with check option)
values
  (9999, 'test', sysdate + 1);
/*多表插入语句*/
create table emp1 as select empno, ename, job from emp where 1 = 2;
create table emp2 as select empno, ename, deptno from emp where 1 = 2;
/*无条件*/
insert all into emp1
  (empno, ename, job)
values
  (empno, ename, job) into emp2
  (empno, ename, deptno)
values
  (empno, ename, deptno)
  select empno, ename, job, deptno from emp where deptno in (10, 20);
/*有条件*/
insert all when job in
  ('SALESMAN', 'MANAGER') then into emp1
  (empno, ename, job)
values
  (empno, ename, job) when deptno in
  ('20', '30') then into emp2
  (empno, ename, deptno)
values
  (empno, ename, deptno)
  select empno, ename, job, deptno from emp;
/*第一条满足的*/
insert first when job in
  ('SALESMAN', 'MANAGER') then into emp1
  (empno, ename, job)
values
  (empno, ename, job) when deptno in
  ('20', '30') then into emp2
  (empno, ename, deptno)
values
  (empno, ename, deptno)
  select empno, ename, job, deptno from emp;
select * from emp1;
select * from emp2;

create table t2(d varchar2(10),des varchar2(50));
create table t1 as
select '熊样，精神不佳' as d1,
       '猫样，温驯听话' as d2,
       '狗样，神气活现' as d3,
       '鸟样，向往明天' as d4,
       '花样，愿你快乐像花一样' as d5
  from dual;

insert all into t2
  (d, des)
values
  ('周一', d1) into t2
  (d, des)
values
  ('周二', d2) into t2
  (d, des)
values
  ('周三', d3) into t2
  (d, des)
values
  ('周四', d4) into t2
  (d, des)
values
  ('周五', d5)
  select d1, d2, d3, d4, d5 from t1;
select * from t2;
/*用其他表中的值更新*/
create table emp2 as select * from emp;
alter table emp2 add dname varchar2(50) default 'noname';
/*错误示范*/
update emp2
   set emp2.dname =
       (select dept.dname
          from dept
         where dept.deptno = emp2.deptno
           and dept.dname in ('ACCOUNTING', 'RESEARCH'));
select * from emp2;
/*正确的查询测试*/
select deptno,
       dname as old_name,
       (select dept.dname
          from dept
         where dept.deptno = emp2.deptno
           and dept.dname in ('ACCOUNTING', 'RESEARCH')) as new_dname,
       case
         when emp2.deptno not in (10, 20) then
          '不该被更新的行'
       end as des
  from emp2
 where exists (select dept.dname
          from dept
         where dept.deptno = emp2.deptno
           and dept.dname in ('ACCOUNTING', 'RESEARCH'));
/*正确的更新*/
update emp2
   set emp2.dname =
       (select dept.dname
          from dept
         where dept.deptno = emp2.deptno
           and dept.dname in ('ACCOUNTING', 'RESEARCH'))
 where exists (select dept.dname
          from dept
         where dept.deptno = emp2.deptno
           and dept.dname in ('ACCOUNTING', 'RESEARCH'));
select * from emp2;
/*正确的更新2*/
update (select emp2.dname, dept.dname as new_dname
          from emp2
         inner join dept
            on dept.deptno = emp2.deptno
         where dept.dname in ('ACCOUNTING', 'RESEARCH'))
   set dname = new_dname;
select * from emp2;
/*正确的更新3*/
merge into emp2
using (select dname, deptno
         from dept
        where dept.dname in ('ACCOUNTING', 'RESEARCH')) dept
on (dept.deptno = emp2.deptno)
when matched then
  update set emp2.dname = dept.dname;
/*合并记录*/
create table bonuses (employee_id number, bonus number default 100);
insert into bonuses
  (employee_id)
  (select e.employee_id
     from hr.employees e, oe.orders o
    where e.employee_id = o.sales_rep_id
    group by e.employee_id);
select * from bonuses order by employee_id;

merge into bonuses d
using (select employee_id, salary, department_id
         from hr.employees
        where department_id = 80) s
on (d.employee_id = s.employee_id)
/*匹配条件d.employee_id = s.employee_id*/
when matched then/*当d表中存在与S对应数据时进行更新或删除*/
  update
     set d.bonus = d.bbonus + s.salary * 0.01 delete
     /*where只能出现一次，如果在这里加了where，delete后面的where就无效*/
   where (s.salary > 8000)/*删除时，只更新s.salary > 8000时的数据*/
when not matched then/*当d表中不存在与S对应的数据时进行新增*/
  insert
    (d.employee_id, d.bonus)
  values
    (s.employee_id, s.salary * 0.01) where
    (s.salary > 8000);/*新增时，只更新s.salary < 8000时的数据，注意这里与前面不同，是d表中不存在对应数据时才新增*/
/*删除违反参照完整性的记录*/
insert into emp2
  (empno, ename, job, mgr, hiredate, sal, comm, deptno)
  select 9999 as empno, ename, job, mgr, hiredate, sal, comm, 99 as deptno
    from emp
   where rownum <= 1;
delete from emp2
 where not exists (select null from dept where dept.deptno = emp2.deptno);
select * from emp;
/*删除名称重复的记录*/
create table dupes (id integer,name varchar2(10));
insert into dupes values(1,'NAPOLEON');
insert into dupes values(2,'DYNAIMTE');
insert into dupes values(3,'DYNAIMTE');
insert into dupes values(4,'SHE SELLS');
insert into dupes values(5,'SEA SHELLS');
insert into dupes values(6,'SEA SHELLS');
insert into dupes values(7,'SEA SHELLS');
/*方法1*/
delete from dupes a
 where exists (select null
          from dupes b
         where b.name = a.name
           and b.id > a.id);
select * from dupes;
delete dupes;
/*方法2*/
delete from dupes a
 where exists (select /*hash_sj*/
         null
          from dupes b
         where b.name = a.name
           and b.rowid > a.rowid);
select * from dupes;
delete dupes;
/*方法3*/
select rowid as rid,
       name,
       row_number() over(partition by name order by id) as seq
  from dupes
 order by 2, 3;
delete from dupes
 where rowid in (select rid
                   from (select rowid as rid,
                                row_number() over(partition by name order by id) as seq
                           from dupes)
                  where seq > 1);

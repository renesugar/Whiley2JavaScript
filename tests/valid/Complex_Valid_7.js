function max3$Y9fFX7i$31(r0, r1, r2){//function(int,int,int) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            if(WyJS.lt(r0, r1, false)){
               control_flow_pc = 1551;
               control_flow_repeat = true;
               continue outer;
            }
            if(WyJS.gt(r0, r2, true)){
               control_flow_pc = 1552;
               control_flow_repeat = true;
               continue outer;
            }
         case 1551:
            var r6 = false;
            control_flow_pc = 1553;
            control_flow_repeat = true;
            continue outer;//goto label1553
         case 1552:
            var r6 = true;
         case 1553:
            var r5 = r6;//assign %5 = %6  : bool
            var r3 = r5;//assign %3 = %5  : bool
            if(WyJS.lt(r1, r0, false)){
               control_flow_pc = 1554;
               control_flow_repeat = true;
               continue outer;
            }
            if(WyJS.gt(r1, r2, true)){
               control_flow_pc = 1555;
               control_flow_repeat = true;
               continue outer;
            }
         case 1554:
            var r8 = false;
            control_flow_pc = 1556;
            control_flow_repeat = true;
            continue outer;//goto label1556
         case 1555:
            var r8 = true;
         case 1556:
            var r7 = r8;//assign %7 = %8  : bool
            var r4 = r7;//assign %4 = %7  : bool
            var r9 = true;
            if(r3 === r9){
               control_flow_pc = 1557;
               control_flow_repeat = true;
               continue outer;
            }
            control_flow_pc = 1558;
            control_flow_repeat = true;
            continue outer;//goto label1558
         case 1557:
            return r0;//return %0 : int
            control_flow_pc = 1559;
            control_flow_repeat = true;
            continue outer;//goto label1559
         case 1558:
            var r10 = true;
            if(r4 === r10){
               control_flow_pc = 1560;
               control_flow_repeat = true;
               continue outer;
            }
            control_flow_pc = 1561;
            control_flow_repeat = true;
            continue outer;//goto label1561
         case 1560:
            return r1;//return %1 : int
            control_flow_pc = 1559;
            control_flow_repeat = true;
            continue outer;//goto label1559
         case 1561:
            return r2;//return %2 : int
         case 1559:
      }
   }
}

function fn$3Ab737klNRWz(r0){//method([int]) -> void
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r8 = new WyJS.Integer(0);
            var r7 = r8;//assign %7 = %8  : int
            var r1 = r7;//assign %1 = %7  : int
            control_flow_pc = -2;
            control_flow_repeat = true;
            break;
         case -2:
            var r9 = new WyJS.Integer(0);
            if(WyJS.gt(r1, r9, true)){
               control_flow_pc = 1562;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1562:
            var r10 = r0.length();//lengthof %10 = %0 : [int]
            if(WyJS.gt(r1, r10, true)){
               control_flow_pc = 1563;
               control_flow_repeat = true;
               continue outer;
            }
            var r12 = r0.getValue(r1);
            var r11 = r12;//assign %11 = %12  : int
            var r2 = r11;//assign %2 = %11  : int
            var r14 = new WyJS.Integer(0);
            var r13 = r14;//assign %13 = %14  : int
            var r3 = r13;//assign %3 = %13  : int
            control_flow_pc = -3;
            control_flow_repeat = true;
            break;
         case -3:
            var r15 = new WyJS.Integer(0);
            if(WyJS.gt(r3, r15, true)){
               control_flow_pc = 1564;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1564:
            var r16 = r0.length();//lengthof %16 = %0 : [int]
            if(WyJS.gt(r3, r16, true)){
               control_flow_pc = 1565;
               control_flow_repeat = true;
               continue outer;
            }
            var r18 = r0.getValue(r3);
            var r17 = r18;//assign %17 = %18  : int
            var r4 = r17;//assign %4 = %17  : int
            var r20 = new WyJS.Integer(0);
            var r19 = r20;//assign %19 = %20  : int
            var r5 = r19;//assign %5 = %19  : int
            control_flow_pc = -4;
            control_flow_repeat = true;
            break;
         case -4:
            var r21 = new WyJS.Integer(0);
            if(WyJS.gt(r5, r21, true)){
               control_flow_pc = 1566;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1566:
            var r22 = r0.length();//lengthof %22 = %0 : [int]
            if(WyJS.gt(r5, r22, true)){
               control_flow_pc = 1567;
               control_flow_repeat = true;
               continue outer;
            }
            var r24 = r0.getValue(r5);
            var r23 = r24;//assign %23 = %24  : int
            var r6 = r23;//assign %6 = %23  : int
            if(WyJS.gt(r2, r6, false)){
               control_flow_pc = 1568;
               control_flow_repeat = true;
               continue outer;
            }
            if(WyJS.lt(r4, r6, true)){
               control_flow_pc = 1569;
               control_flow_repeat = true;
               continue outer;
            }
         case 1568:
            control_flow_pc = 1570;
            control_flow_repeat = true;
            continue outer;//goto label1570
         case 1569:
            var r25 = max3$Y9fFX7i$31(r2, r4, r6);//invoke %25 = (%2, %4, %6) Complex_Valid_7:max3 : function(int,int,int) -> int
            if(WyJS.equals(r25, r6, true)){
               control_flow_pc = 1570;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1570:
            if(WyJS.gt(r2, r4, false)){
               control_flow_pc = 1571;
               control_flow_repeat = true;
               continue outer;
            }
            if(WyJS.lt(r6, r4, true)){
               control_flow_pc = 1572;
               control_flow_repeat = true;
               continue outer;
            }
         case 1571:
            control_flow_pc = 1573;
            control_flow_repeat = true;
            continue outer;//goto label1573
         case 1572:
            var r26 = max3$Y9fFX7i$31(r2, r4, r6);//invoke %26 = (%2, %4, %6) Complex_Valid_7:max3 : function(int,int,int) -> int
            if(WyJS.equals(r26, r4, true)){
               control_flow_pc = 1573;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1573:
            if(WyJS.gt(r4, r6, false)){
               control_flow_pc = 1574;
               control_flow_repeat = true;
               continue outer;
            }
            if(WyJS.lt(r6, r2, true)){
               control_flow_pc = 1575;
               control_flow_repeat = true;
               continue outer;
            }
         case 1574:
            control_flow_pc = 1576;
            control_flow_repeat = true;
            continue outer;//goto label1576
         case 1575:
            var r27 = max3$Y9fFX7i$31(r2, r4, r6);//invoke %27 = (%2, %4, %6) Complex_Valid_7:max3 : function(int,int,int) -> int
            if(WyJS.equals(r27, r2, true)){
               control_flow_pc = 1576;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 1576:
            var r28 = new WyJS.Integer(1);
            var r29 = r5.add(r28);//add %29 = %5, %28 : int
            var r5 = r29;//assign %5 = %29  : int
            control_flow_pc = -4;
            control_flow_repeat = true;
            break;
            control_flow_pc = 1567;
            control_flow_repeat = true;
            break;
         case 1567:
            var r30 = new WyJS.Integer(1);
            var r31 = r3.add(r30);//add %31 = %3, %30 : int
            var r3 = r31;//assign %3 = %31  : int
            control_flow_pc = -3;
            control_flow_repeat = true;
            break;
            control_flow_pc = 1565;
            control_flow_repeat = true;
            break;
         case 1565:
            var r32 = new WyJS.Integer(1);
            var r33 = r1.add(r32);//add %33 = %1, %32 : int
            var r1 = r33;//assign %1 = %33  : int
            control_flow_pc = -2;
            control_flow_repeat = true;
            break;
         case 1563:
      }
   }
}

function test$1A_7VkE(){//method() -> void
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r0 = new WyJS.Integer(1);
            var r1 = new WyJS.Integer(2);
            var r2 = new WyJS.Integer(3);
            var r3 = new WyJS.Integer(4);
            var r4 = new WyJS.Integer(5);
            var r5 = new WyJS.Integer(6);
            var r6 = new WyJS.Integer(7);
            var r7 = new WyJS.Integer(8);
            var r8 = new WyJS.List([r0, r1, r2, r3, r4, r5, r6, r7], new WyJS.Type.List(new WyJS.Type.Int()));
            fn$3Ab737klNRWz(r8);//invoke %(%8) Complex_Valid_7:fn : method([int]) -> void
      }
   }
}


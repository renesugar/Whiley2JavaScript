function f$Z9bFaA$WL1f$$K$Z6$k0FO$J3$l0FS$35$g0kR$w4$L0FN$k5$d0$O$w4$l$kC4$m0$Q$Z5$n0$S(r0){//function(TypeEquals_Valid_17:rlist) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            if(WyJS.is(r0, new WyJS.Type.Real())){
               control_flow_pc = 930;
               control_flow_repeat = true;
               continue outer;
            }
            control_flow_pc = 931;
            control_flow_repeat = true;
            continue outer;//goto label931
         case 930:
            var r1 = new WyJS.Integer(0);
            return r1;//return %1 : int
            control_flow_pc = 932;
            control_flow_repeat = true;
            continue outer;//goto label932
         case 931:
            var r2 = r0.length();//lengthof %2 = %0 : [int]
            return r2;//return %2 : int
         case 932:
            return;
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
            var r1 = new WyJS.Real(123.0);
            var r0 = f$Z9bFaA$WL1f$$K$Z6$k0FO$J3$l0FS$35$g0kR$w4$L0FN$k5$d0$O$w4$l$kC4$m0$Q$Z5$n0$S(r1);//invoke %0 = (%1) TypeEquals_Valid_17:f : function(TypeEquals_Valid_17:rlist) -> int
            var r2 = new WyJS.Integer(0);
            if(WyJS.equals(r0, r2, true)){
               control_flow_pc = 933;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 933:
            var r4 = new WyJS.Real(1.23);
            var r3 = f$Z9bFaA$WL1f$$K$Z6$k0FO$J3$l0FS$35$g0kR$w4$L0FN$k5$d0$O$w4$l$kC4$m0$Q$Z5$n0$S(r4);//invoke %3 = (%4) TypeEquals_Valid_17:f : function(TypeEquals_Valid_17:rlist) -> int
            var r5 = new WyJS.Integer(0);
            if(WyJS.equals(r3, r5, true)){
               control_flow_pc = 934;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 934:
            var r7 = new WyJS.Integer(1);
            var r8 = new WyJS.Integer(2);
            var r9 = new WyJS.Integer(3);
            var r10 = new WyJS.List([r7, r8, r9], new WyJS.Type.List(new WyJS.Type.Int()));
            var r6 = f$Z9bFaA$WL1f$$K$Z6$k0FO$J3$l0FS$35$g0kR$w4$L0FN$k5$d0$O$w4$l$kC4$m0$Q$Z5$n0$S(r10);//invoke %6 = (%10) TypeEquals_Valid_17:f : function(TypeEquals_Valid_17:rlist) -> int
            var r11 = new WyJS.Integer(3);
            if(WyJS.equals(r6, r11, true)){
               control_flow_pc = 935;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 935:
            return;
      }
   }
}

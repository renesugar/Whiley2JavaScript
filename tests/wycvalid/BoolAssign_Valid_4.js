function f(r0, r1){//function(int,int) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            if(r0  ==  r1){
               control_flow_pc = 62;
               control_flow_repeat = true;
               continue outer;
            }
            var r4 = false;//const %4 = false : bool
            control_flow_pc = 63;
            control_flow_repeat = true;
            continue outer;
         case 62:
            var r4 = true;//const %4 = true : bool
         case 63:
            var r3 = r4;//assign %3 = %4  : bool
            var r2 = r3;//assign %2 = %3  : bool
            var r5 = true;//const %5 = true : bool
            if(r2  ==  r5){
               control_flow_pc = 64;
               control_flow_repeat = true;
               continue outer;
            }
            control_flow_pc = 65;
            control_flow_repeat = true;
            continue outer;
         case 64:
            var r6 = 1;//const %6 = 1 : int
            return r6;//return %6 : int
            control_flow_pc = 66;
            control_flow_repeat = true;
            continue outer;
         case 65:
            var r7 = r0 + r1;//add %7 = %0, %1 : int
            return r7;//return %7 : int
         case 66:
      }
   }
}

function g(r0, r1){//function(int,int) -> int
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            if(r0  >=  r1){
               control_flow_pc = 67;
               control_flow_repeat = true;
               continue outer;
            }
            var r4 = false;//const %4 = false : bool
            control_flow_pc = 68;
            control_flow_repeat = true;
            continue outer;
         case 67:
            var r4 = true;//const %4 = true : bool
         case 68:
            var r3 = r4;//assign %3 = %4  : bool
            var r2 = r3;//assign %2 = %3  : bool
            var r5 = true;//const %5 = true : bool
            if(r2  ==  r5){
               control_flow_pc = 69;
               control_flow_repeat = true;
               continue outer;
            }
            var r6 = r0 + r1;//add %6 = %0, %1 : int
            return r6;//return %6 : int
            control_flow_pc = 70;
            control_flow_repeat = true;
            continue outer;
         case 69:
            var r7 = 1;//const %7 = 1 : int
            return r7;//return %7 : int
         case 70:
      }
   }
}

function test(){//method() -> void
   var control_flow_repeat = true;
   var control_flow_pc = -1;
   outer:
   while(control_flow_repeat){
      control_flow_repeat = false
      switch(control_flow_pc){
         case -1 :
            var r1 = 1;//const %1 = 1 : int
            var r2 = 1;//const %2 = 1 : int
            var r0 = f(r1, r2);
            var r3 = 1;//const %3 = 1 : int
            if(r0  ==  r3){
               control_flow_pc = 71;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 71:
            var r5 = 0;//const %5 = 0 : int
            var r6 = 0;//const %6 = 0 : int
            var r4 = f(r5, r6);
            var r7 = 1;//const %7 = 1 : int
            if(r4  ==  r7){
               control_flow_pc = 72;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 72:
            var r9 = 4;//const %9 = 4 : int
            var r10 = 345;//const %10 = 345 : int
            var r8 = f(r9, r10);
            var r11 = 349;//const %11 = 349 : int
            if(r8  ==  r11){
               control_flow_pc = 73;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 73:
            var r13 = 1;//const %13 = 1 : int
            var r14 = 1;//const %14 = 1 : int
            var r12 = g(r13, r14);
            var r15 = 1;//const %15 = 1 : int
            if(r12  ==  r15){
               control_flow_pc = 74;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 74:
            var r17 = 0;//const %17 = 0 : int
            var r18 = 0;//const %18 = 0 : int
            var r16 = g(r17, r18);
            var r19 = 1;//const %19 = 1 : int
            if(r16  ==  r19){
               control_flow_pc = 75;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 75:
            var r21 = 4;//const %21 = 4 : int
            var r22 = 345;//const %22 = 345 : int
            var r20 = g(r21, r22);
            var r23 = 349;//const %23 = 349 : int
            if(r20  ==  r23){
               control_flow_pc = 76;
               control_flow_repeat = true;
               continue outer;
            }
            throw {name: 'Assert Failed', message: 'fail'}
         case 76:
      }
   }
}

test();
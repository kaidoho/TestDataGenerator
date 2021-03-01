package tdg

import spinal.core._
import spinal.lib._




class tdg_frame_trigger(cnt_size: BitCount) extends Component {


  val enable_i  = in Bool
  val fps_i    = in UInt(cnt_size)
  val start_frame_o  = out Bool


  val sig_cnt   = Reg(UInt(cnt_size)) init(0)
  val sig_start = Reg(Bool) init(False)
  val sig_trigger = Reg(Bool) init(False)
  val sig_wrap_around = Reg(UInt(cnt_size)) init(0)


  start_frame_o := sig_trigger

  when( enable_i.rise(False))
  {
    sig_cnt := 0
    sig_trigger := False
    sig_wrap_around := fps_i
  } otherwise {
    when (sig_cnt === sig_wrap_around) {
      sig_cnt := 0
      sig_trigger := !sig_trigger
    } otherwise {
      sig_cnt := (sig_cnt + 1)
    }
  }


}
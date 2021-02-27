package tdg

import spinal.core._
import spinal.lib._





//Hardware definition
class TopTdg (cols: Int, rows: Int) extends Component {


  val m_axis = master(Axi4Stream(Axi4StreamConfig(32)))

  val ft = new tdg_frame_trigger(32 bits)
  val fr = new tdg_frame_controller(cols, rows)


  val fps_i   =  in cloneOf(ft.fps_i)
  val enable_i =  in cloneOf(ft.enable_i)

  ft.fps_i   := fps_i
  ft.enable_i := enable_i

  fr.start_frame_i := ft.start_frame_o
  fr.enable_i := enable_i



  m_axis.tdata := fr.m_axis.tdata
  m_axis.tvalid := fr.m_axis.tvalid
  m_axis.tlast := fr.m_axis.tlast
  fr.m_axis.tready := m_axis.tready

}


//Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one. This configuration can be resued everywhere
object MySpinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC, resetActiveLevel=LOW))

//Generate the MyTopLevel's VHDL
object MyTopLevelVhdl {
  def main(args: Array[String]) {

    def telgram = new Ibeo_MessageContent_F0(192,3)

    telgram.get_size_in_bits()

    //SpinalVhdl(new MyTopLevel)
    //MySpinalConfig.generateVhdl(new TopTdg(192 ,80))
    MySpinalConfig.generateVerilog(new TopTdg(192 ,80))
  }
}



//Generate the MyTopLevel's Verilog using the above custom configuration.
object MyTopLevelVerilogWithCustomConfig {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new TopTdg(192,80))
  }
}
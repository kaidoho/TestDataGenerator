package tdg

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._

class tdg_frame_controller(cols: Int, rows: Int) extends Component {

  val enable_i  = in Bool
  val start_frame_i  = in Bool
  val m_axis = master(Axi4Stream(Axi4StreamConfig(32)))

  val storage = tdg_frame_storage(cols,rows,3)
  storage.start_transmit_i := False


  val fsm = new StateMachine{
    val idle = new State with EntryPoint
    val transferA200 = new State
    val transferF0 = new State

    idle
      .whenIsActive{
        when( start_frame_i.rise(False) && enable_i ) {
          goto(transferA200)
        }
      }

    transferA200
      .onEntry(storage.start_transmit_i := False)
      .whenIsActive{
        storage.start_transmit_i := True
        when( storage.transmit_done_o) {goto(transferF0)        }
      }

    transferF0
      .onEntry(storage.start_transmit_i := False)
      .whenIsActive{
        storage.start_transmit_i := True
        when( storage.transmit_done_o) {goto(idle) }
      }
  }

  m_axis.tdata := storage.m_axis.tdata
  m_axis.tvalid := storage.m_axis.tvalid
  m_axis.tlast := storage.m_axis.tlast
  storage.m_axis.tready := m_axis.tready
}
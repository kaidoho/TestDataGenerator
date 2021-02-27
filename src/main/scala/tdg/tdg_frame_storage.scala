package tdg

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._

case class tdg_frame_storage(Columns: Int, rows: Int, PointsPerVector: Int) extends Component {


  //val wordCount = roundUp(Ibeo_MessageContent_A200(Columns,PointsPerVector).get_size_in_bits(),32)
  //val mem = Mem(Bits(32 bits), wordCount = wordCount)

  val wordCount = 8
  val mem = Mem(Bits(32 bits), wordCount = wordCount) init(Seq(0,1,2,3,4,5,6,7))

  val start_transmit_i = in Bool
  val transmit_done_o = out Bool


  val sig_readValid = Reg(Bool) init(False)


  val sig_writeValid = Reg(Bool) init(False)
  val sig_writeAddress  = Reg(UInt(log2Up(wordCount) bits)) init(0)
  val sig_writeData  = Bits(32 bits)

  val cfg = Axi4StreamConfig(32)
  val m_axis = master(Axi4Stream(cfg))


  val sig_read_data = Reg(Bits(32 bits)) init(0)
  val sig_read_valid = Reg(Bool) init(False)
  val sig_read_tlast = Reg(Bool) init(False)

  val sig_transmit_done = Reg(Bool) init(False)
  val sig_word_counter = Reg(UInt(log2Up(wordCount) bits)) init(0)
  val sig_word_stop    = Reg(UInt(log2Up(wordCount) bits)) init(wordCount-2)

  val fsm = new StateMachine {
    val idle = new State with EntryPoint
    val transfer = new State
    val transferLast = new State
    val signalDone = new State

    idle
      .whenIsActive {
        sig_read_valid := False
        sig_read_tlast := False
        sig_transmit_done := False
        sig_word_counter := 0

        when(start_transmit_i.rise(False)) {
          goto(transfer)
        }
      }
    transfer
      .whenIsActive {
        sig_read_valid := True;
        when(m_axis.tready && start_transmit_i) {
          sig_word_counter := sig_word_counter + 1
          sig_read_data:= mem.readSync(sig_word_counter)
          if (sig_word_counter == sig_word_stop) {
            goto(transferLast)
          }
        }
      }
    transferLast
      .whenIsActive {
        when(m_axis.tready && start_transmit_i) {
          sig_word_counter := sig_word_counter + 1
          sig_read_tlast := True
          sig_read_data:= mem.readSync(sig_word_counter)
          goto(signalDone)
        }
      }
    signalDone
      .whenIsActive {
        sig_read_tlast := False
        sig_transmit_done := True
        goto(idle)
      }
  }


  m_axis.tdata  := sig_read_data
  m_axis.tvalid := sig_read_valid
  m_axis.tlast  := sig_read_tlast
  transmit_done_o := sig_transmit_done



}
/** ****************************************************************************
 * This file describes the Axi4 interface
 *
 * _________________________________________________________________________
 * | Global | Write Data | Write Addr | Write Resp | Read Data  | Read Addr  |
 * |   -    |    w       |    aw      |      b     |     r      |     ar     |
 * |-------------------------------------------------------------------------|
Signal Status Notes
TVALID  Required
TREADY  Optional TREADY is optional, but highly recommended.
TDATA   Optional
TSTRB   Optional Not typically used by end-point IP; available for sparse stream
        signalling. Note: For marking packet remainders, TKEEP
        rather than TSTRB is used.
TKEEP   Absent Null bytes are only used for signaling packet remainders.
        Leading or intermediate Null bytes are generally not
        supported.
TLAST   Optional
TID     Optional Not typically used by end-point IP; available for use by
        infrastructure IP.
TDEST   Optional Not typically used by end-point IP; available for use by
        infrastructure IP.
TUSER   Optional
 *
 */

package tdg


import spinal.core._
import spinal.lib._

object Axi4Stream {
  /** Transfer type constant */
  def IDLE   = B"00"
  def BUSY   = B"01"
  def NONSEQ = B"10"
  def SEQ    = B"11"
}


case class Axi4StreamConfig(dataWidth: Int){

  def dataType     = Bits(dataWidth bits)
  def bytePerWord  = dataWidth / 8
  def symboleRange = log2Up(bytePerWord) - 1 downto 0

}

/**
 * AHB lite Master interface
 */
case class Axi4StreamMaster(config: Axi4StreamConfig) extends Bundle with IMasterSlave {

  //  Data
  val tdata    = Bits(config.dataWidth bits)
  val tlast    = Bool
  val tvalid   = Bool
  val tready   = Bool



  override def asMaster(): Unit = {
    out(tdata,tlast,tvalid)
    in(tready)
  }


  def toAxi4Stream(): Axi4Stream = {
    val slave = Axi4Stream(config)

    slave.tdata    := this.tdata



    slave
  }
}


/**
 * AHB lite interface
 */
case class Axi4Stream(config: Axi4StreamConfig) extends Bundle with IMasterSlave {

  //  Data
  val tdata    = Bits(config.dataWidth bits)
  val tlast    = Bool
  val tvalid   = Bool
  val tready   = Bool



  override def asMaster(): Unit = {
    out(tdata,tlast,tvalid)
    in(tready)
  }


  /** Connect two AhbLite3 bus together with the resized of the address */
  def <<(that: Axi4Stream): Unit = {

    that.tdata    := this.tdata
    this.tdata    := that.tdata

  }

  def >>(that: Axi4Stream): Unit = that << this


}

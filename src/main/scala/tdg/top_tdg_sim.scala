package tdg

import spinal.core._
import spinal.lib._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random

object spinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC, resetActiveLevel=LOW))
//val spinalConfig = SpinalConfig(defaultClockDomainFrequency = FixedFrequency(100 MHz))

object MyTopLevelSim {
  def main(args: Array[String]) {


    val f_in = 100000000 //Hz
    val req_fps  = 100 //Hz

    val step_time = 10000 //10ns
    val sim_time =  100 * 1000 * 10


    SimConfig.withConfig(spinalConfig).withFstWave.doSim(new TopTdg(190,80)){ dut =>
      //Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = step_time) // 10ns

      dut.fps_i #= f_in / req_fps / 2

      for(idx <- 0 to 5){

        dut.enable_i #= false
        dut.m_axis.tready #= false
        dut.clockDomain.waitRisingEdge()

      }
      for(idx <- 0 to 5){

        dut.enable_i #= true
        dut.m_axis.tready #= false
        dut.clockDomain.waitRisingEdge()

      }
      for(idx <- 0 to sim_time){

        dut.enable_i #= true
        dut.m_axis.tready #= true
        dut.clockDomain.waitRisingEdge()

      }
      /*
      for(idx <- 0 to 500) {

        dut.fps_i := 25
        dut.enable_i := True
        dut.m_axis.tready:= True
        dut.clockDomain.waitRisingEdge()

        println( dut.m_axis.tdata )

      }
*/
/*
        //Wait a rising edge on the clock
        dut.clockDomain.waitRisingEdge()

        //Check that the dut values match with the reference model ones
        val modelFlag = modelState == 0 || dut.io.cond1.toBoolean
        assert(dut.io.state.toInt == modelState)
        assert(dut.io.flag.toBoolean == modelFlag)

        //Update the reference model value
        if(dut.io.cond0.toBoolean) {
          modelState = (modelState + 1) & 0xFF
        }
      }
    */
    }

  }
}

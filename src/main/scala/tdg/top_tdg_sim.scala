package tdg

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random



object MyTopLevelSim {
  def main(args: Array[String]) {
    SimConfig.withWave.doSim(new TopTdg(190,80)){dut =>
      //Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      var modelState = 0
      for(idx <- 0 to 5) {

        dut.fps_i := 25
        dut.enable_i := False
        dut.m_axis.tready:= False
        dut.clockDomain.waitRisingEdge()
      }
      for(idx <- 0 to 500) {

        dut.fps_i := 25
        dut.enable_i := True
        dut.m_axis.tready:= True
        dut.clockDomain.waitRisingEdge()

        println( dut.m_axis.tdata )

      }

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

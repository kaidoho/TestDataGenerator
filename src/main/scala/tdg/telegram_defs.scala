package tdg



class FrontEnd_BaseType(){
  val content =   Map[String, Int]()
  def get_size_in_bits(): Int =
  {
    var ret = 0
    for ((k,v) <- content) {
      ret = ret + v
    }

    printf("Size %d\n", ret)

    return ret
  }
}


case class FrontEnd_MeasurementPoint() extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "PulseWidthRatio" -> 4,
    "Distance" -> 12,
    "AVGroundPeak" -> 8,
    "Intensity" -> 32
  )
}

case class FrontEnd_MeasurementVector(PointsPerVector: Int) extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "Valid" -> 1,
    "NoiseFloot" -> 7,
    "Blinding" -> 1,
    "BinCountMean" -> 7,
    "Data" ->  FrontEnd_MeasurementPoint().get_size_in_bits() * PointsPerVector

  )
}

case class FrontEnd_MessageContent_A200(Columns: Int,PointsPerVector: Int) extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "FieldOfView" -> 16*8,
    "SensorGeometry" -> 24*8,
    "MountingPose" -> 50*8,
    "OpticParams" -> 92*8,
    "SensorID" -> 64,
    "SensorType" -> 16,
    "RefID" -> 8*8,
    "TimeClockFreq" -> 16,
    "FirmwareID" -> 8*8,
    "StaticPeakDetection" -> 315*8,
    "PulseWidthCompression" ->  2*8,
    "LutDistanceWeight" -> 1792 * 8,
    "LutConversion" -> 259 * 8
  )
}


case class FrontEnd_MessageContent_F0(Columns: Int,PointsPerVector: Int) extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "Mode" -> 8,
    "FrameStatus" -> 3*8,
    "FrameID" -> 4*8,
    "RefID" -> 8*8,
    "FrameStartTime" ->  12*8,
    "Reserved" -> 100 * 8
  )
}


case class FrontEnd_MessageContent_F1(Columns: Int,PointsPerVector: Int) extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "MeasurementType" -> 8,
    "Row" -> 8,
    "RowStatus" -> 8,
    "RefGroupID" -> 8,
    "FrameID" ->  32,
    "StartTime" ->  32,
    "EndTime" -> 32,
    "Measurements" ->  FrontEnd_MeasurementVector(PointsPerVector).get_size_in_bits() * Columns
  )
}

case class FrontEnd_MessageContent_F2(Columns: Int,PointsPerVector: Int) extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "Reserved0" -> 8,
    "FrameEndStatus" -> 3*8,
    "FrameID" ->  32,
    "BlockageActive" ->  1*8,
    "BlockageData" ->  12*8,
    "Reserved1" -> 107*8
  )
}

class FrontEnd_MessageHeader() extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "VersionNumber" -> 8,
    "PackageType" -> 16,
    "SensorNumber" -> 8
  )
}

class FrontEnd_MessageFooter() extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "CRC" -> 32
  )
}

class ModuleTelegram() extends FrontEnd_BaseType{
  override val content =   Map[String, Int](
    "VersionNumber" -> 8,
    "PackageType" -> 16,
    "SensorNumber" -> 8,
    "Crc" -> 32
  )
}


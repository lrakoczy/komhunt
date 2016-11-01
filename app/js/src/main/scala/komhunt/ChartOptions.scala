package komhunt

import com.highcharts.HighchartsAliases._
import com.highcharts.HighchartsUtils._
import com.highcharts.config.{HighchartsConfig, _}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Object, UndefOr, |}

@ScalaJSDefined
class ChartConfiguration(name: String, timeStamps: Seq[String], windSpeed: Seq[Double]) extends HighchartsConfig {
  override val chart: Cfg[Chart] = new Chart {
  }

  override val title: Cfg[Title] = new Title {
    override val text: UndefOr[String] = name
  }

  override val xAxis: Cfg[XAxis] = new XAxis {
    override val categories: CategoriesCfg = js.Array(timeStamps : _*)
  }

  override val series: SeriesCfg = js.Array[AnySeries](
    new SeriesColumn {
      override val name: UndefOr[String] = "Wind speed"
      override val data: SeriesCfgData[SeriesColumnData] = js.Array[SeriesColumnData](windSpeed.map(d => new SeriesColumnData {
        override val color: UndefOr[|[String, Object]] = "black"
        override val y: UndefOr[Double] = d
      }) : _*)
    }
  )
}
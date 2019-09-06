package com.ec.survey.controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.OutputStream;

@Controller
@RequestMapping("/graphics")
public class GraphicsController extends BasicController {

	@RequestMapping(value = "/pie.png")
	public void pie(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String pscore = request.getParameter("v");
		String pmax = request.getParameter("m");
		
		if (pscore == null || pmax == null) return;
		
		int score = Integer.parseInt(pscore);
		int max = Integer.parseInt(pmax);
		
		DefaultPieDataset pieDataset = new DefaultPieDataset();
	    pieDataset.setValue("Score", score);
	    pieDataset.setValue("Other", max - score);

        JFreeChart chart = ChartFactory.createPieChart("", pieDataset, false, false, false);
        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setCircular(true);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(null);
        plot.setBaseSectionPaint(Color.blue);
        plot.setSectionPaint("Score",new Color(51,122,183));
        plot.setSectionPaint("Other", Color.lightGray);
        plot.setShadowGenerator(null);        
        plot.setShadowPaint(Color.white);
                
        try {
            response.setContentType("image/png");
            OutputStream out = response.getOutputStream();

            ChartUtilities.writeChartAsPNG(out , chart , 130, 130);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }		
	}
		
}

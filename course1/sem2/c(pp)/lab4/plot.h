#ifndef PLOT_H
#define PLOT_H

#include <QtDataVisualization/Q3DSurface>
#include <QtDataVisualization>

class Plot : public QObject
{
    Q_OBJECT
public:
    Plot();
    QtDataVisualization::QSurface3DSeries *m_SincFirstSeries;
    QtDataVisualization::QSurfaceDataProxy *m_SincFirstProxy;
    QtDataVisualization::QSurface3DSeries *m_SincSecondSeries;
    QtDataVisualization::QSurfaceDataProxy *m_SincSecondProxy;
    int m_sampleCountX = 50;
    int m_sampleCountZ = 50;
    const float m_sampleMin = -10.0f;
    const float m_sampleMax = 10.0f;
    float m_rangeMinX;
    float m_rangeMinZ;
    float m_stepX;
    float m_stepZ;

    void recalc();

private:
    void eval_Sinc1();
    void eval_Sinc2();
};

#endif // PLOT_H

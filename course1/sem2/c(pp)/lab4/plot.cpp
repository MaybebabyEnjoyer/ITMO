#include "plot.h"

#include <QtCore/qmath.h>

using namespace QtDataVisualization;

Plot::Plot()
{
    m_SincFirstProxy = new QSurfaceDataProxy();
    m_SincFirstSeries = new QSurface3DSeries(m_SincFirstProxy);
    eval_Sinc1();

    m_SincSecondProxy = new QSurfaceDataProxy();
    m_SincSecondSeries = new QSurface3DSeries(m_SincSecondProxy);
    eval_Sinc2();
}

void Plot::eval_Sinc1()
{
    float stepX = (m_sampleMax - m_sampleMin) / float(m_sampleCountX - 1);
    float stepZ = (m_sampleMax - m_sampleMin) / float(m_sampleCountZ - 1);

    QSurfaceDataArray *dataArray = new QSurfaceDataArray;
    dataArray->reserve(m_sampleCountZ);
    for (int i = 0; i < m_sampleCountZ; i++) {
        QSurfaceDataRow *newRow = new QSurfaceDataRow(m_sampleCountX);
        float z = qMin(m_sampleMax, (i * stepZ + m_sampleMin));
        int index = 0;
        for (int j = 0; j < m_sampleCountX; j++) {
            float x = qMin(m_sampleMax, (j * stepX + m_sampleMin));
            float R = qSqrt(z * z + x * x);
            float y = qSin(R) / R;
            (*newRow)[index++].setPosition(QVector3D(x, y, z));
        }
        *dataArray << newRow;
    }

    m_SincFirstProxy->resetArray(dataArray);
}

void Plot::eval_Sinc2()
{
    float stepX = (m_sampleMax - m_sampleMin) / float(m_sampleCountX - 1);
    float stepZ = (m_sampleMax - m_sampleMin) / float(m_sampleCountZ - 1);

    QSurfaceDataArray *dataArray = new QSurfaceDataArray;
    dataArray->reserve(m_sampleCountZ);
    for (int i = 0; i < m_sampleCountZ; i++) {
        QSurfaceDataRow *newRow = new QSurfaceDataRow(m_sampleCountX);
        float z = qMin(m_sampleMax, (i * stepZ + m_sampleMin));
        int index = 0;
        for (int j = 0; j < m_sampleCountX; j++) {
            float x = qMin(m_sampleMax, (j * stepX + m_sampleMin));
            float y = (qSin(x) / x) * (qSin(z) / z);
            (*newRow)[index++].setPosition(QVector3D(x, y, z));
        }
        *dataArray << newRow;
    }

    m_SincSecondProxy->resetArray(dataArray);
}

void Plot::recalc()
{
    eval_Sinc1();
    eval_Sinc2();
}

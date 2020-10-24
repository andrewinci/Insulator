package insulator.di.factories

import insulator.CachedFactory
import insulator.di.components.ClusterComponent
import insulator.di.components.DaggerSubjectComponent
import insulator.di.components.SubjectComponent
import insulator.kafka.model.Subject
import javax.inject.Inject

class SubjectComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<Subject, SubjectComponent>({ subject: Subject ->
        DaggerSubjectComponent.factory().build(clusterComponent, subject)
    })

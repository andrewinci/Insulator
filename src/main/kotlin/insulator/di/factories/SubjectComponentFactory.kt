package insulator.di.factories

import insulator.di.components.ClusterComponent
import insulator.di.components.DaggerSubjectComponent
import insulator.di.components.SubjectComponent
import insulator.lib.kafka.model.Subject
import javax.inject.Inject

class SubjectComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<Subject, SubjectComponent>({ subject: Subject ->
        DaggerSubjectComponent.factory().build(clusterComponent, subject)
    })

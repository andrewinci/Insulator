package insulator.di

import insulator.lib.configuration.model.Cluster

var currentCluster: Cluster = Cluster.empty()

const val CONFIG_FILE_NAME = "Insulator.cfg"
const val VERSION_PROPERTY = "app.version"
const val GITHUB_REPO =
    """https://github.com/andrea-vinci/Insulator"""
const val LATEST_RELEASE_API_ENDPOINT =
    """https://api.github.com/repos/andrea-vinci/Insulator/releases/latest"""

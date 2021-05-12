package com.ec.survey.model.survey.ecf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeUUIDAndName {

    @JsonProperty("typeUUID")
    private String typeUUID;

    @JsonProperty("typeName")
    private String typeName;

    public TypeUUIDAndName(String name, String uuid) {
        this.typeName = name;
        this.typeUUID = uuid;
    }

    public String getTypeUUID() {
        return typeUUID;
    }

    public void setTypeUUID(String typeUUID) {
        this.typeUUID = typeUUID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "TypeUUIDAndName [typeName=" + typeName + ", typeUUID=" + typeUUID + "]";
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        result = prime * result + ((typeUUID == null) ? 0 : typeUUID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeUUIDAndName other = (TypeUUIDAndName) obj;
        if (typeName == null) {
            if (other.typeName != null)
                return false;
        } else if (!typeName.equals(other.typeName))
            return false;
        if (typeUUID == null) {
            if (other.typeUUID != null)
                return false;
        } else if (!typeUUID.equals(other.typeUUID))
            return false;
        return true;
    }

}
